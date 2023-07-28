<template>
  <v-card>
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
        <v-btn v-if="$auth.loggedIn && canSubmit === true" class="mb-4 ml-4" outlined @click="showRatingDialog = true">Datenprodukt bewerten</v-btn>
      </v-card-actions>
      <v-dialog v-model="showDataDialog" persistent width="auto" max-width="400">
        <data-product-use-data-card :short-key="dataProductDetail.shortKey" @on-close-dialog="showDataDialog = false" />
      </v-dialog>
      <v-dialog v-model="showRatingDialog" persistent width="auto" max-width="400">
        <data-product-edit-rating-form :short-key="dataProductDetail.shortKey" @on-rating-added="refreshRatings()" @on-close-dialog="showRatingDialog = false" />
      </v-dialog>
      <v-container v-for="(rating, index) in dataProductDetail.ratings" :key="index">
        <data-product-rating-card
            :title="rating.title"
            :comment="rating.comment"
            :rating="rating.rating"
            :user-name="rating.userName"
            :submitted="rating.submitted"
            :short-key="dataProductDetail.shortKey"
            @on-rating-deleted="refreshRatings()"
        />
      </v-container>
    </v-card>
  </v-card>
</template>

<script>
  import {
    getDataProduct,
    getDataProductImage, getDataProductRatingCanSubmit,
    getDataProductRatings
  } from "~/middleware/dataProductService";
  import DataProductRatingCard from "~/components/DataProductRatingCard.vue";
  import DataProductDetailCard from "~/components/DataProductDetailCard.vue";
  import DataProductEditRatingForm from "~/components/DataProductEditRatingForm.vue";
  import DataProductUseDataCard from "~/components/DataProductUseDataCard.vue";

  export default {
    components: {DataProductUseDataCard, DataProductEditRatingForm, DataProductDetailCard, DataProductRatingCard},
    props: {
      shortKey: {
        type: String,
        required: false,
        default: ''
      }
    },
    data() {
      return {
        showDataDialog: false,
        showRatingDialog: false,
        showDeleteRating: false,
        dataProductDetail: null, // Initialize to null to indicate data is not yet loaded
        canSubmit: true
      }
    },
    async fetch() {
      this.dataProductDetail = await this.fetchDataProductDetail(this.shortKey);
      if(this.$auth.loggedIn){
        this.canSubmit = await getDataProductRatingCanSubmit(this.$axios, this.shortKey);
      }
    },
    watch: {
      shortKey() {
        this.dataProductDetail = null // Reset to null when shortKey changes to indicate data is not yet loaded
      },
      async dataProductDetail() {
        // Load data when dataProductDetail changes from null to an object
        if (this.dataProductDetail === null) {
          this.dataProductDetail = await this.fetchDataProductDetail(this.shortKey)
        }
      },
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
            submitted: new Date(rawDataProductRating.submitted).toLocaleDateString('ge-GE')
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
      async refreshRatings() {
        this.dataProductDetail = await this.fetchDataProductDetail(this.shortKey);
        this.canSubmit = await getDataProductRatingCanSubmit(this.$axios, this.shortKey);
      }
    }
  }
</script>