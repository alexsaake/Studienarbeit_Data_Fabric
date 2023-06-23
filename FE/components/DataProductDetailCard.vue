<template>
  <v-card id="dataProductDetail">
    <template v-if="!dataProductDetail">
      <v-progress-circular
        :size="120"
        indeterminate
        color="white"
      ></v-progress-circular>
    </template>
    <template v-else>
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
        <v-card>{{ dataProductDetail.sourceLink }}</v-card> Zuletzt
        aktualisiert: {{ dataProductDetail.lastUpdated }}<br />
        Kategorie: {{ dataProductDetail.category }}<br />
        Zugriff: {{ dataProductDetail.accessRight }}
      </v-card-text>
      <v-card-actions>
        <v-btn class="mb-4 ml-4" outlined @click="showOverlay = true">
          Datenprodukt abrufen
        </v-btn>
      </v-card-actions>
      <v-overlay :value="showOverlay" :opacity="1">
        <a
          style="color: white"
          target="_blank"
          :href="'/api/Gateway/DataProducts/' + shortKey"
          >Datenprodukt via RESTful API abrufen</a
        >
        <v-layout justify-center>
          <v-btn class="mt-2" @click="showOverlay = false"> Zurück </v-btn>
        </v-layout>
      </v-overlay>
    </template>
  </v-card>
</template>

<script>
import dataProductImageProvider from '~/middleware/dataProductImageProvider'
import dataProductDetailProvider from '~/middleware/dataProductDetailProvider'

export default {
  props: {
    shortKey: {
      type: String,
      required: false,
      default: '',
    },
  },
  data() {
    return {
      showOverlay: false,
      dataProductDetail: null, // Initialize to null to indicate data is not yet loaded
    }
  },
  async fetch() {
    this.dataProductDetail = await this.fetchDataProductDetail(this.shortKey)
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
      const rawDataProductDetail = await dataProductDetailProvider(
        this.$axios,
        shortKey
      )
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
        image: await dataProductImageProvider(
          this.$axios,
          rawDataProductDetail.shortKey
        ),
      }
    },
  },
}
</script>

<style lang="css">
#dataProductDetail {
  width: 50%;
  height: 50%;
  transform: translate(50%, 0);
}
</style>
