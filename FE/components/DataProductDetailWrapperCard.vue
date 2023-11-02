<template>
  <v-card v-if="!dataProductDetail" class="my-progress">
    <v-progress-circular :size="120" indeterminate color="white"/>
  </v-card>
  <v-card v-else class="my-details">
    <v-card>
      <data-product-detail-card
        :id="dataProductDetail.id"
        :title="dataProductDetail.title"
        :short-description="dataProductDetail.shortDescription"
        :description="dataProductDetail.description"
        :source="dataProductDetail.source"
        :source-link="dataProductDetail.sourceLink"
        :last-updated="dataProductDetail.lastUpdated"
        :category="dataProductDetail.category"
        :access-right="dataProductDetail.accessRight"
        :avg-rating="dataProductDetail.avgRating"
        :image="dataProductDetail.image"
        :user-name="dataProductDetail.userName"
        @on-data-product-deleted="$emit('on-data-product-deleted');"
        @on-edit-data-product="$emit('on-edit-data-product')"
      />
      <v-card-actions>
        <v-btn @click="onOpenUseData">Datenprodukt abrufen</v-btn>
        <v-btn v-if="$auth.loggedIn && canSubmit === true" @click="onCreateRating">Datenprodukt bewerten</v-btn>
      </v-card-actions>
      <v-card-actions v-if="screenWidth<600">
        <v-btn @click="$emit('on-close-data-product');">Zurück</v-btn>
      </v-card-actions>
      <v-container class="pa-0">
        <v-row  v-if="dataProductDetail.ratings.length == 0" justify="center" >Keine eigenen Bewertungen gefunden</v-row>
        <v-row no-gutters>
          <v-col v-for="(rating, index) in dataProductDetail.ratings" :key="index" cols="12">
            <data-product-rating-card
                :id="dataProductDetail.id"
                :title="rating.title"
                :comment="rating.comment"
                :rating="rating.rating"
                :user-name="rating.userName"
                :submitted="rating.submitted"
                :is-edited="rating.isEdited"
                @on-rating-deleted="refreshRatings"
                @on-edit-rating="onUpdateRating(rating)"
            />
          </v-col>
        </v-row>
      </v-container>
    </v-card>
    <v-card v-if="showUseDataDialog" class="my-dialog">
      <data-product-use-data-card :id="dataProductDetail.id" @on-close-dialog="onCloseUseData" />
    </v-card>
    <v-card v-if="showRatingDialog" class="my-dialog">
      <data-product-edit-rating-card :id="dataProductDetail.id" :is-update="isUpdate" :existing-rating="existingRating" @on-rating-added="onRatingAdded" @on-close-dialog="onCloseRating" />
    </v-card>
  </v-card>
</template>

<script>
import {
  getDataProduct, getDataProductAvgRatings,
  getDataProductImage, getDataProductRatingCanSubmit,
  getDataProductRatings
} from "~/middleware/dataProductService";
  import DataProductRatingCard from "~/components/DataProductRatingCard.vue";
  import DataProductDetailCard from "~/components/DataProductDetailCard.vue";
  import DataProductEditRatingCard from "~/components/DataProductEditRatingCard.vue";
  import DataProductUseDataCard from "~/components/DataProductUseDataCard.vue";

  export default {
    components: {
      DataProductUseDataCard, DataProductEditRatingCard, DataProductDetailCard, DataProductRatingCard},
    props: {
      id: {
        type: Number,
        required: true,
        default: -1
      }
    },
    data() {
      return {
        showUseDataDialog: false,
        showRatingDialog: false,
        showDeleteRating: false,
        dataProductDetail: null, // Initialize to null to indicate data is not yet loaded
        canSubmit: true,
        isUpdate: false,
        existingRating: null,
        screenWidth: null,
      }
    },
    async fetch() {
      if(this.id !== -1)
      {
        this.dataProductDetail = await this.fetchDataProductDetail(this.id);
        if(this.$auth.loggedIn)
        {
          this.canSubmit = await getDataProductRatingCanSubmit(this.$axios, this.id);
        }
      }
      else
      {
        this.dataProductDetail = null // Reset to null when id changes to indicate data is not yet loaded
      }
    },
    mounted() {
      this.updateScreenWidth();
      this.onScreenResize();
    },
    methods: {
      async fetchDataProductDetail(id) {
        const rawDataProductDetail = await getDataProduct(this.$axios, id);
        const rawDataProductRatings = await getDataProductRatings(this.$axios, id);
        const dataProductRatings = [];
        for (const rawDataProductRating of rawDataProductRatings) {
          dataProductRatings.push({
            title: rawDataProductRating.title,
            comment: rawDataProductRating.comment,
            rating: rawDataProductRating.rating,
            userName: rawDataProductRating.userName,
            submitted: new Date(rawDataProductRating.submitted).toLocaleDateString('ge-GE'),
            isEdited: rawDataProductRating.isEdited,
          });
        }
        return {
          id: rawDataProductDetail.id,
          title: rawDataProductDetail.title,
          shortDescription: rawDataProductDetail.shortDescription,
          description: rawDataProductDetail.description,
          source: rawDataProductDetail.source,
          sourceLink: rawDataProductDetail.sourceLink,
          lastUpdated: new Date(rawDataProductDetail.lastUpdated).toLocaleDateString('ge-GE'),
          category: rawDataProductDetail.category,
          accessRight: rawDataProductDetail.accessRight,
          image: await getDataProductImage(this.$axios, rawDataProductDetail.id),
          ratings: dataProductRatings,
          avgRating: await getDataProductAvgRatings(this.$axios, rawDataProductDetail.id),
          userName: rawDataProductDetail.userName
        };
      },
      onOpenUseData()
      {
        this.showUseDataDialog = true;
      },
      onCloseUseData()
      {
        this.showUseDataDialog = false;
      },
      async refreshRatings()
      {
        this.dataProductDetail = await this.fetchDataProductDetail(this.id);
        if(this.$auth.loggedIn)
        {
          this.canSubmit = await getDataProductRatingCanSubmit(this.$axios, this.id);
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
      onCloseDataProduct()
      {
        this.$emit('on-close-data-product');
      },
      onScreenResize() {
        window.addEventListener("resize", () => {
          this.updateScreenWidth();
        });
      },
      updateScreenWidth() {
        this.screenWidth = window.innerWidth;
      },
    }
  }
</script>

<style scoped>
  .my-progress
  {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .my-details
  {
    height: 100%;
  }
  .my-dialog
  {
    position: fixed;
    width: 50%;
    height: 100%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    overflow-y: scroll;
  }
  /*@media screen and (max-width: 900px) {*/
  /*    .my-dialog {*/
  /*        transform: translate(-25%, -25%);*/
  /*        width: 75%;*/
  /*        top: 37.5%;*/
  /*        left: 37.5%;*/
  /*    }*/
  /*}*/
  @media screen and (max-width: 600px) {
      .my-dialog {
          transform: unset;
          width: 100%;
          top: 0;
          left: 0;
      }
  }
</style>