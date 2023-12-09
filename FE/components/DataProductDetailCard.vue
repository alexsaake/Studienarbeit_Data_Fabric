<template>
  <v-card class="loading-spinner-wrapper">
    <v-card v-if="isLoading">
      <v-progress-circular :size="120" indeterminate color="white"/>
    </v-card>
    <v-card v-else>
      <v-img :src="imageFileName" height="300px" />
      <v-card-actions style="position: absolute; top: 310px; right: 20px;">
        <v-btn v-show="$auth.loggedIn && $auth.user.userName === userName" icon @click="onShowConfirmDeleteRating" >
          <v-icon>mdi-delete</v-icon>
        </v-btn>
        <v-btn v-show="$auth.loggedIn && $auth.user.userName === userName" icon @click="$emit('on-edit-data-product')">
          <v-icon>mdi-lead-pencil</v-icon>
        </v-btn>
      </v-card-actions>
      <v-card-title style="word-break: break-word">{{ title }}</v-card-title>
      <v-card-subtitle>{{ shortDescription }}</v-card-subtitle>
      <v-card-text>
        <v-container class="pa-0">
          <v-row no-gutters>
            <v-col>{{ description }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="2">Ersteller</v-col>
            <v-col>{{ userName }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="2">Quelle</v-col>
            <v-col>{{ source }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="2">Quellen-Link:</v-col>
            <v-col>{{ sourceLink }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="2">Zuletzt aktualisiert:</v-col>
            <v-col>{{ lastUpdated.toLocaleDateString('de-GE') }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="2">Kategorie</v-col>
            <v-col>{{ category }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="2">Zugriff</v-col>
            <v-col>{{ accessRight }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="3">Durchschnittliche Bewertung </v-col>
            <v-col>{{ averageRating.toFixed(2) }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="3">Erstelldatum </v-col>
            <v-col>{{ createdOn.toString().split("-").reverse().join('.')}}</v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card v-if="showConfirmDeleteDialog" v-click-outside="onCloseConfirmDeleteRating" class="my-dialog">
        <v-card-title>Sicher löschen?</v-card-title>
        <v-card-actions>
          <v-btn @click="onConfirmDeleteRating">Löschen</v-btn>
          <v-btn @click="onCloseConfirmDeleteRating">Abbrechen</v-btn>
        </v-card-actions>
      </v-card>
    </v-card>
  </v-card>
</template>

<script>
import {
  deleteDataProduct,
  getDataProductDetails
} from "~/middleware/dataProductService";

  export default {
    name: 'DataProductDetailCard',
    props:
    {
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
    data()
    {
      return {
        showConfirmDeleteDialog: false,
        description: '',
        source: '',
        sourceLink: '',
        isLoading: true,
        createdOn: ''
      }
    },
    async fetch() {
      await this.fetchDataProductDetail();
    },
    methods:{
      async fetchDataProductDetail() {
        const dataProductDetails = await getDataProductDetails(this.$axios, this.id);
        this.description = dataProductDetails.description;
        this.source = dataProductDetails.source;
        this.sourceLink = dataProductDetails.sourceLink;
        this.createdOn = dataProductDetails.createdOn;

        this.isLoading = false;
      },
      async onConfirmDeleteRating()
      {
        if(this.$auth.loggedIn)
        {
          await deleteDataProduct(this.$axios, this.id)
              .then(() => {
                this.showConfirmDeleteDialog = false;
                this.$emit('on-data-product-deleted');
              });
        }
      },
      onShowConfirmDeleteRating()
      {
        this.showConfirmDeleteDialog = true;
      },
      onCloseConfirmDeleteRating()
      {
        this.showConfirmDeleteDialog = false;
      },

    }
  }
</script>

<style scoped>
.my-dialog
{
  position: absolute;
  width: 100%;
  height: 100%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>