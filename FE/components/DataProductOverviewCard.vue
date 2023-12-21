<template>
  <v-card class="d-flex flex-column" @click="$emit('on-select-data-product', id, imageFileName, title, shortDescription, lastUpdated, accessRight, averageRating, userName)">
    <v-card v-if="isLoading" class="loading-spinner-wrapper">
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
            <v-col cols="8">Zuletzt aktualisiert</v-col>
            <v-col cols="4" class="value">{{ lastUpdated.toLocaleDateString('ge-GE') }}</v-col>
          </v-row>
          <v-row  no-gutters>
            <v-col cols="8">Zugriff</v-col>
            <v-col cols="4" class="value">{{ accessRight }}</v-col>
          </v-row>
          <v-row no-gutters>
            <v-col cols="8">Ersteller</v-col>
            <v-col cols="4" class="value">{{ userName }}</v-col>
          </v-row>
          <v-row>
            <v-col style="display: flex;align-items: center;justify-content: end" >
              <v-rating :value="averageRating" readonly half-increments>
              </v-rating>
              <div>({{averageRating.toFixed(1)}})</div>
            </v-col>
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
<style scoped>
.value{
    font-style: italic;
}
</style>