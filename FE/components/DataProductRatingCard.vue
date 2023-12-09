<template>
  <v-card class="my-card">
    <v-card v-if="isLoading" class="loading-spinner-wrapper">
      <v-progress-circular :size="120" indeterminate color="white"/>
    </v-card>
    <v-card v-else>
      <v-card-title v-if="title !== null" style="word-break: break-word">{{title}}</v-card-title>
      <v-card-text v-if="comment !== null" class="text-pre-wrap">{{comment}}</v-card-text>
      <v-rating :value="rating" readonly></v-rating>
      <v-card-text>
        <v-container class="pa-0">
          <v-row no-gutters>
            <v-col cols="2">Verfasser:</v-col>
            <v-col>{{ userName }}</v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col cols="2">Datum:</v-col>
            <v-col>{{ submitted.toLocaleDateString('ge-GE') }}</v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-text v-show="isEdited">
        bearbeitet
      </v-card-text>
      <v-card-actions>
        <v-btn v-show="$auth.loggedIn && $auth.user.userName === userName" icon @click="onShowConfirmDeleteRating">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
        <v-btn v-show="$auth.loggedIn && $auth.user.userName === userName" icon @click="$emit('on-edit-rating', ratingId, title, comment, rating)">
          <v-icon>mdi-lead-pencil</v-icon>
        </v-btn>
      </v-card-actions>
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
import {deleteDataProductRating, getDataProductRating} from "~/middleware/dataProductService";

  export default
  {
    name: 'DataProductRatingCard',
    props:
    {
      ratingId: Number
    },
    data()
    {
      return {
        title: '',
        comment: '',
        rating: 0,
        userName: '',
        submitted: new Date(),
        showConfirmDeleteDialog: false,
        isEdited: false,
        isLoading: true
      }
    },
    async fetch() {
      await this.fetchRating();
    },
    methods: {
      async fetchRating() {
        const rawRating = await getDataProductRating(this.$axios, this.ratingId);
        this.title = rawRating.title;
        this.comment = rawRating.comment;
        this.rating = rawRating.rating;
        this.userName = rawRating.userName;
        this.submitted = new Date(rawRating.submitted);
        this.isEdited = rawRating.isEdited;

        this.isLoading = false;
      },
      async onConfirmDeleteRating() {
        if(this.$auth.loggedIn) {
          await deleteDataProductRating(this.$axios, this.ratingId)
              .then(() => {
                this.showConfirmDeleteDialog = false;
                this.$emit('on-rating-deleted');
              });
        }
      },
      onShowConfirmDeleteRating() {
        this.showConfirmDeleteDialog = true;
      },
      onCloseConfirmDeleteRating() {
        this.showConfirmDeleteDialog = false;
      }
    }
  }
</script>

<style scoped>
  .my-card {
    position: relative;
  }
  .my-dialog {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
</style>