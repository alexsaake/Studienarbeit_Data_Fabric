<template>
  <v-card>
    <v-container>
      <v-form v-model="form" @submit.prevent="onSubmitRating">
        <h1>Bitte bewerten Sie das Datenprodukt</h1>
        <v-text-field v-model="title" type="text" class="form-control" label="Titel" clearable :counter="ratingTitleMaxLength" :maxlength="ratingTitleMaxLength"></v-text-field>
        <v-textarea v-model="comment" type="text" class="form-control" label="Rezension" clearable :counter="ratingCommentMaxLength" :maxlength="ratingCommentMaxLength"></v-textarea>
        <v-input :value="rating" :rules="[required]"><v-rating v-model="rating" class="form-control"></v-rating></v-input>
        <v-btn :disabled="!form" type="submit">Absenden</v-btn>
        <v-btn @click="cancelRating()">Zurück</v-btn>
      </v-form>
    </v-container>
  </v-card>
</template>

<script>
import {
  getDataProductRatingMaxLengths,
  setDataProductRating,
  updateDataProductRating
} from "~/middleware/dataProductService";

  export default {
    name: 'DataProductEditRatingForm',
    props: {
      dataProductId: {
        type: Number,
        required: true,
        default: -1
      },
      isUpdate: {
        type: Boolean,
        required: true,
        default: false
      },
      existingRating: {
        type: Object,
        default: null
      }
    },
    data() {
      return {
        ratingId: -1,
        title: '',
        comment: '',
        rating: 0,
        form: false,
        ratingTitleMaxLength: 0,
        ratingCommentMaxLength: 0
      }
    },
    async fetch() {
      if(this.isUpdate)
      {
        this.ratingId = this.existingRating.ratingId;
        this.title = this.existingRating.title;
        this.comment = this.existingRating.comment;
        this.rating = this.existingRating.rating;
      }
      if(this.$auth.loggedIn) {
        const maxLengths = await getDataProductRatingMaxLengths(this.$axios);
        this.ratingTitleMaxLength = maxLengths.title;
        this.ratingCommentMaxLength = maxLengths.comment;
      }
    },
    methods: {
      required (v) {
        return !!v || 'Field is required'
      },
      async onSubmitRating() {
        if(!this.isUpdate)
        {
          await setDataProductRating(this.$axios, this.dataProductId, this.title, this.comment, this.rating)
              .then(() => {
                this.cancelRating();
                this.$emit('on-rating-added');
              });
        }
        else
        {
          await updateDataProductRating(this.$axios, this.ratingId, this.title, this.comment, this.rating)
              .then(() => {
                this.cancelRating();
                this.$emit('on-rating-added');
              });
        }
      },
      cancelRating() {
        this.title = '';
        this.comment = '';
        this.rating = 0;
        this.form = false;
        this.$emit('on-close-dialog');
      }
    }
  }
</script>