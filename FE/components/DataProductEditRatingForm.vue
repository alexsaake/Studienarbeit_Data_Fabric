<template>
  <v-card>
    <v-form v-model="form" @submit.prevent="onSubmitRating">
      <h1>Please rate the data product</h1>
      <v-text-field v-model="title" type="text" class="form-control" label="Title" clearable></v-text-field>
      <v-textarea v-model="comment" type="text" class="form-control" label="Comment" clearable :counter="ratingCommentMaxLength" :maxlength="ratingCommentMaxLength"></v-textarea>
      <v-input :value="rating" :rules="[required]"><v-rating v-model="rating" class="form-control"></v-rating></v-input>
      <v-btn :disabled="!form" type="submit">Submit</v-btn>
      <v-btn @click="cancelRating()">Zurück</v-btn>
    </v-form>
  </v-card>
</template>

<script>
  import {getDataProductRatingCommentMaxLength, setDataProductRating} from "~/middleware/dataProductService";

  export default {
    name: 'DataProductEditRatingForm',
    props: {
      shortKey: {
        type: String,
        required: false,
        default: ''
      }
    },
    data() {
      return {
        title: '',
        comment: '',
        rating: 0,
        form: false,
        ratingCommentMaxLength: 0
      }
    },
    async fetch() {
      if(this.$auth.loggedIn){
        this.ratingCommentMaxLength = await getDataProductRatingCommentMaxLength(this.$axios);
      }
    },
    methods: {
      required (v) {
        return !!v || 'Field is required'
      },
      async onSubmitRating() {
        await setDataProductRating(this.$axios, this.shortKey, this.title, this.comment, this.rating)
          .then(() => {
            this.cancelRating();
            this.$emit('on-rating-added');
          });
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