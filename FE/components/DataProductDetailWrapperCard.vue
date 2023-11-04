<template>
  <v-card class="my-details">
    <v-card v-if="isLoading">
      <v-progress-circular :size="120" indeterminate color="white"/>
    </v-card>
    <v-card v-else>
      <v-card v-if="showUseDataDialog" class="my-dialog">
        <data-product-use-data-card :id="id" @on-close-dialog="onCloseUseData" />
      </v-card>
      <v-card v-else-if="showRatingDialog" class="my-dialog">
        <data-product-edit-rating-card :id="id" :is-update="isUpdate" :existing-rating="existingRating" @on-rating-added="onRatingAdded" @on-close-dialog="onCloseRating" />
      </v-card>
      <v-card v-else>
        <data-product-detail-card
            :image-file-name="imageFileName"
            :id="id"
            :title="title"
            :short-description="shortDescription"
            :last-updated="lastUpdated"
            :category="category"
            :access-right="accessRight"
            :average-rating="averageRating"
            :user-name="userName"
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
          <v-row v-if="ratings == null">
            <v-progress-circular :size="120" indeterminate color="white"/>
          </v-row>
          <v-row v-else-if="ratings.length === 0" justify="center" >Keine eigenen Bewertungen gefunden</v-row>
          <v-row else no-gutters>
            <v-col v-for="(rating, index) in ratings" :key="index" cols="12">
              <v-lazy :min-height="200" :options="{'threshold':0.5}" transition="fade-transition">
                <data-product-rating-card
                    :rating-id="rating.id"
                    @on-rating-deleted="refreshRatings"
                    @on-edit-rating="refreshRatings"
                />
              </v-lazy>
            </v-col>
          </v-row>
        </v-container>
      </v-card>
    </v-card>
  </v-card>
</template>

<script>
import {
  getDataProductRatingCanSubmit, getDataProductRatings,
} from "~/middleware/dataProductService";
  import DataProductRatingCard from "~/components/DataProductRatingCard.vue";
  import DataProductDetailCard from "~/components/DataProductDetailCard.vue";
  import DataProductEditRatingCard from "~/components/DataProductEditRatingCard.vue";
  import DataProductUseDataCard from "~/components/DataProductUseDataCard.vue";

  export default {
    components: {
      DataProductUseDataCard, DataProductEditRatingCard, DataProductDetailCard, DataProductRatingCard},
    props: {
      imageFileName: String,
      id: Number,
      title: String,
      shortDescription: String,
      lastUpdated: Date,
      category: String,
      accessRight: String,
      averageRating: Number,
      userName: String
    },
    data() {
      return {
        showUseDataDialog: false,
        showRatingDialog: false,
        showDeleteRating: false,
        ratings: null, // Initialize to null to indicate data is not yet loaded
        canSubmit: true,
        isUpdate: false,
        existingRating: null,
        screenWidth: null,
        isLoading: true
      }
    },
    async fetch() {
      await this.refreshRatings();
    },
    mounted() {
      this.updateScreenWidth();
      this.onScreenResize();
    },
    methods: {
      async fetchRatings() {
        const rawRatings = await getDataProductRatings(this.$axios, this.id);
        const ratings = [];
        for (const rawRating of rawRatings) {
          ratings.push({
            id: rawRating.id
          });
        }
        this.ratings = ratings;
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
        this.isLoading = true;
        await this.fetchRatings();
        if(this.$auth.loggedIn)
        {
          this.canSubmit = await getDataProductRatingCanSubmit(this.$axios, this.id);
        }
        this.isLoading = false;
      },
      onCreateRating()
      {
        this.isUpdate = false;
        this.existingRating = null;
        this.showRatingDialog = true
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