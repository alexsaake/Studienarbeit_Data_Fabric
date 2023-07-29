<template>
  <v-card>
    <v-card-title v-if="title !== null" style="word-break: break-word">{{title}}</v-card-title>
    <v-card-text v-if="comment !== null" class="text-pre-wrap">{{comment}}</v-card-text>
    <v-rating :value="rating" readonly></v-rating>
    <v-card-text>
      Verfasser: {{userName}}<br>
      Datum: {{submitted}}
    </v-card-text>
    <v-card-text v-show="isEdited">
      bearbeitet
    </v-card-text>
    <v-card-actions>
      <v-btn v-show="$auth.user.userName === userName" icon @click="showConfirmDeleteDialog = true">
        <v-icon>mdi-delete</v-icon>
      </v-btn>
      <v-btn v-show="$auth.user.userName === userName" icon @click="$emit('on-edit-rating')">
        <v-icon>mdi-lead-pencil</v-icon>
      </v-btn>
    </v-card-actions>
    <v-dialog v-model="showConfirmDeleteDialog" persistent width="auto">
      <v-card>
        <v-card-title>Sicher löschen?</v-card-title>
        <v-card-actions>
          <v-btn @click="onConfirmDeleteRating()">Löschen</v-btn>
          <v-btn @click="showConfirmDeleteDialog = false">Abbrechen</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script>
  import {deleteDataProductRating} from "~/middleware/dataProductService";

  export default
  {
    name: 'DataProductRatingCard',
    props:
    {
      title: String,
      comment: String,
      rating: Number,
      userName: String,
      submitted: String,
      isEdited: Boolean,
      shortKey: String
    },
    data()
    {
      return {
        showConfirmDeleteDialog: false
      }
    },
    methods: {
      async onConfirmDeleteRating() {
        await deleteDataProductRating(this.$axios, this.shortKey)
            .then(() => {
              this.showConfirmDeleteDialog = false;
              this.$emit('on-rating-deleted');
            });
      }
    }
  }
</script>