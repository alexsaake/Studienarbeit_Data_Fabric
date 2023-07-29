<template>
  <v-card class="dataProductDetail">
    <v-card v-if="!dataProductDetail">
      <v-progress-circular
        :size="120"
        indeterminate
        color="white"
      ></v-progress-circular>
    </v-card>
    <v-card v-else>
      <v-img
        :src="'data:text/plain;base64,' + dataProductDetail.image"
        height="300px"
      />
      <v-card-title style="word-break: break-word">{{
        dataProductDetail.title
      }}</v-card-title>
      <v-card-subtitle>{{
        dataProductDetail.shortDescription
      }}</v-card-subtitle>
      <v-card-text>
        {{ dataProductDetail.description }}<br />
        Quelle: {{ dataProductDetail.source }}<br />
        Quellen-Link:
        {{ dataProductDetail.sourceLink }}
        Zuletzt aktualisiert: {{ dataProductDetail.lastUpdated }}<br />
        Kategorie: {{ dataProductDetail.category }}<br />
        Zugriff: {{ dataProductDetail.accessRight }}
      </v-card-text>
      <v-card-actions>
        <v-btn class="mb-4 ml-4" outlined @click="showDataOverlay = true">
          Datenprodukt abrufen
        </v-btn>
        <v-btn v-if="$auth.loggedIn && newRating.hasAlreadyRated === false" class="mb-4 ml-4" outlined @click="showRatingOverlay=true">
          Datenprodukt bewerten
        </v-btn>
      </v-card-actions>
      <v-container v-for="(rating, index) in dataProductDetail.ratings" :key="index">
        <data-product-rating-card
            :data-product-rating="rating"
        />
      </v-container>
    </v-card>
    <v-overlay :value="showDataOverlay" :opacity="1">
      <v-container>
        <v-row>
          <v-col cols="12" md="6">
            <v-row>
              <v-col cols="12" md="6" class="py-1">
                <v-btn class="ml-4" outlined>
                  <a
                      style="color: white; text-decoration: unset"
                      target="_blank"
                      :href="'/api/Gateway/DataProduct/' + shortKey + '/Data'"
                  >RESTful API</a
                  >
                </v-btn>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12" md="6" class="py-1">
                <v-btn class="ml-4" outlined>
                  <a
                      style="color: white; text-decoration: unset"
                      target="_blank"
                      :href="'dataTableView?shortKey=' + shortKey"
                  >Vorschau (in Tabelle)</a
                  >
                </v-btn>
              </v-col>
            </v-row>
          </v-col>
          <v-col cols="12" md="6">
            <v-layout row align-center>
              <v-img src="insights.png" contain max-width="80px" class="mx-4" />

              <v-btn class="mx-4" outlined>
                <a
                    style="color: white; text-decoration: unset"
                    target="_blank"
                    :href="'/api/Gateway/DataProduct/' + shortKey + '/Insights'"
                >Insights</a
                >
              </v-btn>
            </v-layout>
          </v-col>
        </v-row>
      </v-container>

      <v-layout justify-center>
        <v-btn class="mt-2" @click="showDataOverlay = false"> Zurück </v-btn>
      </v-layout>
    </v-overlay>
    <v-overlay :value="showRatingOverlay" :opacity="1">
      <v-form v-model="newRating.form" @submit.prevent="onSubmit">
        <h1>Please rate the data product</h1>
        <v-text-field v-model="newRating.title" type="text" class="form-control" label="Title" clearable></v-text-field>
        <v-text-field v-model="newRating.comment" type="text" class="form-control" label="Comment" clearable></v-text-field>
        <v-input :value="newRating.rating" :rules="[required]"><v-rating v-model="newRating.rating" class="form-control"></v-rating></v-input>
        <v-btn :disabled="!newRating.form" type="submit">Submit</v-btn>
      </v-form>

      <v-layout justify-center>
        <v-btn class="mt-2" @click="showRatingOverlay=false">Zurück</v-btn>
      </v-layout>
    </v-overlay>
  </v-card>
</template>

<script>
import {
  getDataProduct,
  getDataProductImage,
  getDataProductRatings, getHasAlreadyRatedDataProduct,
  setDataProductRating
} from "~/middleware/dataProductService";
import DataProductRatingCard from "~/components/DataProductRatingCard.vue";

export default {
  components: {DataProductRatingCard},
  props: {
    shortKey: {
      type: String,
      required: false,
      default: '',
    },
  },
  data() {
    return {
      showDataOverlay: false,
      showRatingOverlay: false,
      dataProductDetail: null, // Initialize to null to indicate data is not yet loaded
      newRating: {
        title: '',
        comment: '',
        rating: 0,
        form: false,
        hasAlreadyRated: false
      }
    }
  },
  async fetch() {
    this.dataProductDetail = await this.fetchDataProductDetail(this.shortKey);
    this.newRating.hasAlreadyRated = await getHasAlreadyRatedDataProduct(this.$axios, this.shortKey);
  },
  watch: {
    shortKey() {
      this.dataProductDetail = null // Reset to null when shortKey changes to indicate data is not yet loaded
    },
    async dataProductDetail() {
      // Load data when dataProductDetail changes from null to an object
      if (this.dataProductDetail === null) {
        this.dataProductDetail = await this.fetchDataProductDetail(
          this.shortKey
        )
      }
    },
  },
  fetchOnServer: false,
  methods: {
    async fetchDataProductDetail(shortKey) {
      const rawDataProductDetail = await getDataProduct(
        this.$axios,
        shortKey
      );
      const rawDataProductRatings = await getDataProductRatings(
          this.$axios,
          shortKey
      );
      return {
        shortKey: rawDataProductDetail.shortKey,
        title: rawDataProductDetail.title,
        shortDescription: rawDataProductDetail.shortDescription,
        description: rawDataProductDetail.description,
        source: rawDataProductDetail.source,
        sourceLink: rawDataProductDetail.sourceLink,
        lastUpdated: new Date(
          rawDataProductDetail.lastUpdated
        ).toLocaleDateString('ge-GE'),
        category: rawDataProductDetail.category,
        accessRight: rawDataProductDetail.accessRight,
        image: await getDataProductImage(
          this.$axios,
          rawDataProductDetail.shortKey
        ),
        ratings: rawDataProductRatings
      };
    },
    async onSubmit() {
      await setDataProductRating(this.$axios, this.shortKey, this.newRating.title, this.newRating.comment, this.newRating.rating)
          .then(() => {
            window.location.reload();
          })
    },
    required (v) {
      return !!v || 'Field is required'
    }
  },
}
</script>

<style lang="css">
  .dataProductDetail {
    width: 50%;
    transform: translate(50%, 0);
  }
</style>
