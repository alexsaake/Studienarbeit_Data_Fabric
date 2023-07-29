<template>
  <v-overlay v-if="shortKey !== ''" width="100%" height="100%">
    <v-card width="50%" height="100%" v-click-outside="onClickOutside()">
      <v-card v-if="!dataProductDetail">
        <v-progress-circular
          :size="120"
          indeterminate
          color="white"
        ></v-progress-circular>
      </v-card>
      <v-card v-else>
        <data-product-detail-card
          :short-key="dataProductDetail.shortKey"
          :title="dataProductDetail.title"
          :short-description="dataProductDetail.shortDescription"
          :description="dataProductDetail.description"
          :source="dataProductDetail.source"
          :source-link="dataProductDetail.sourceLink"
          :last-updated="dataProductDetail.lastUpdated"
          :category="dataProductDetail.category"
          :access-right="dataProductDetail.accessRight"
          :image="dataProductDetail.image"
        />
        <v-card-actions>
          <v-btn class="mb-4 ml-4" outlined @click="showDataDialog = true">Datenprodukt abrufen</v-btn>
          <v-btn v-show="$auth.loggedIn && canSubmit === true" class="mb-4 ml-4" outlined @click="onCreateRating()">Datenprodukt bewerten</v-btn>
        </v-card-actions>
        <v-dialog v-model="showDataDialog" persistent width="auto" max-width="400">
          <data-product-use-data-card :short-key="dataProductDetail.shortKey" @on-close-dialog="showDataDialog = false" />
        </v-dialog>
        <v-dialog v-model="showRatingDialog" persistent width="auto" max-width="400">
          <data-product-edit-rating-card :short-key="dataProductDetail.shortKey" :is-update="isUpdate" :existing-rating="existingRating" @on-rating-added="onRatingAdded()" @on-close-dialog="onCloseRating()" />
        </v-dialog>
        <v-container v-for="(rating, index) in dataProductDetail.ratings" :key="index">
          <data-product-rating-card
              :title="rating.title"
              :comment="rating.comment"
              :rating="rating.rating"
              :user-name="rating.userName"
              :submitted="rating.submitted"
              :is-edited="rating.isEdited"
              :short-key="dataProductDetail.shortKey"
              @on-rating-deleted="refreshRatings()"
              @on-edit-rating="onUpdateRating(rating)"
          />
        </v-container>
      </v-card>
    </v-card>
  </v-overlay>
</template>

<script>
  import {
    getDataProduct,
    getDataProductImage, getDataProductRatingCanSubmit,
    getDataProductRatings
  } from "~/middleware/dataProductService";
  import DataProductRatingCard from "~/components/DataProductRatingCard.vue";
  import DataProductDetailCard from "~/components/DataProductDetailCard.vue";
  import DataProductEditRatingCard from "~/components/DataProductEditRatingCard.vue";
  import DataProductUseDataCard from "~/components/DataProductUseDataCard.vue";

  export default {
    components: {DataProductUseDataCard, DataProductEditRatingCard, DataProductDetailCard, DataProductRatingCard},
    props: {
      shortKey: {
        type: String,
        required: true,
        default: ''
      }
    },
    data() {
      return {
        showDataDialog: false,
        showRatingDialog: false,
        showDeleteRating: false,
        dataProductDetail: null, // Initialize to null to indicate data is not yet loaded
        canSubmit: true,
        isUpdate: false,
        existingRating: null
      }
    },
    watch: {
      async shortKey()
      {
        if(this.shortKey !== '')
        {
          this.dataProductDetail = await this.fetchDataProductDetail(this.shortKey);
          if(this.$auth.loggedIn)
          {
            this.canSubmit = await getDataProductRatingCanSubmit(this.$axios, this.shortKey);
          }
        }
        else
        {
          this.dataProductDetail = null // Reset to null when shortKey changes to indicate data is not yet loaded
          this.isMounted = false;
        }
      }
    },
    fetchOnServer: false,
    methods: {
      async fetchDataProductDetail(shortKey) {
        const rawDataProductDetail = await getDataProduct(this.$axios, shortKey);
        const rawDataProductRatings = await getDataProductRatings(this.$axios, shortKey);
        const dataProductRatings = [];
        for (const rawDataProductRating of rawDataProductRatings) {
          dataProductRatings.push({
            title: rawDataProductRating.title,
            comment: rawDataProductRating.comment,
            rating: rawDataProductRating.rating,
            userName: rawDataProductRating.userName,
            submitted: new Date(rawDataProductRating.submitted).toLocaleDateString('ge-GE'),
            isEdited: rawDataProductRating.isEdited
          });
        }
        return {
          shortKey: rawDataProductDetail.shortKey,
          title: rawDataProductDetail.title,
          shortDescription: rawDataProductDetail.shortDescription,
          description: rawDataProductDetail.description,
          source: rawDataProductDetail.source,
          sourceLink: rawDataProductDetail.sourceLink,
          lastUpdated: new Date(rawDataProductDetail.lastUpdated).toLocaleDateString('ge-GE'),
          category: rawDataProductDetail.category,
          accessRight: rawDataProductDetail.accessRight,
          image: await getDataProductImage(this.$axios, rawDataProductDetail.shortKey),
          ratings: dataProductRatings
        };
      },
      async refreshRatings()
      {
        this.dataProductDetail = await this.fetchDataProductDetail(this.shortKey);
        if(this.$auth.loggedIn)
        {
          this.canSubmit = await getDataProductRatingCanSubmit(this.$axios, this.shortKey);
        }
      },
      onCreateRating()
      {
        this.isUpdate = false;
        this.existingRating = null;
        this.showRatingDialog = true
      },
      onUpdateRating(rating)
      {
        this.isUpdate = true;
        this.existingRating = rating;
        this.showRatingDialog = true;
      },
      onRatingAdded()
      {
        this.refreshRatings();
        this.onCloseRating();
      },
      onCloseRating()
      {
        this.showRatingDialog = false;
        this.isUpdate = false;
        this.existingRating = null;
      },
      onClickOutside()
      {
        this.$emit('on-close-data-product');
      }
    }
  }
</script>