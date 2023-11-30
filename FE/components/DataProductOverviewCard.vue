<template>
  <v-card class="d-flex flex-column" @click="$emit('on-select-data-product', id, imageFileName, title, shortDescription, lastUpdated, accessRight, averageRating, userName)">
    <v-card v-if="isLoading">
      <v-progress-circular :size="120" indeterminate color="white"/>
    </v-card>
    <v-card v-else>
      <v-img :src="imageFileName" height="100px" />
      <v-card-title style="word-break: break-word">{{ title }}</v-card-title>
      <v-card-subtitle>{{ shortDescription }}</v-card-subtitle>
      <v-spacer />
      <v-card-text>
        <v-container class="pa-0">
          <v-row no-gutters>
            <v-col>Zuletzt aktualisiert:</v-col>
            <v-col>{{ lastUpdated.toLocaleDateString('ge-GE') }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col>Zugriff</v-col>
            <v-col>{{ accessRight }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col>Durchschnittliche Bewertung</v-col>
            <v-col><pre>{{ averageRating.toFixed(2) }}</pre></v-col>
            <v-col><v-rating :value="averageRating" readonly half-increments></v-rating></v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col>Ersteller</v-col>
            <v-col>{{ userName }}</v-col>
          </v-row>
        </v-container>
      </v-card-text>
    </v-card>
  </v-card>
</template>

<script>
import {getDataProduct} from "~/middleware/dataProductService";

  export default
  {
    name: 'DataProductOverviewCard',
    props:
    {
      id: Number,
      title: String,
      userName: String,
      lastUpdated: Date,
      averageRating: Number,
      accessRightsCatalogue: Object
    },
    data() {
      return {
        imageFileName: '',
        shortDescription: '',
        accessRight: '',
        isLoading: true
      }
    },
    async created() {
      await this.fetchData()
    },
    methods: {
      async fetchData() {
        const dataProductSummary = await getDataProduct(this.$axios, this.id);
        if(dataProductSummary.imageFileName === null) {
          this.imageFileName = "defaultImage.jpg";
        } else {
          this.imageFileName = dataProductSummary.imageFileName;
        }
        this.shortDescription = dataProductSummary.shortDescription;
        this.accessRight = this.accessRightsCatalogue[dataProductSummary.accessRightId];

        this.isLoading = false;
      }
    }
  }
</script>
