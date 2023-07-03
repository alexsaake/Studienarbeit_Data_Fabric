<template>
  <v-card>
    <v-card-title style="word-break: break-word" v-if="dataProductRating.title !== null">{{dataProductRating.title}}</v-card-title>
    <v-card-subtitle v-if="dataProductRating.comment !== null">{{dataProductRating.comment}}</v-card-subtitle>
    <v-rating :value="dataProductRating.rating" readonly></v-rating>
    <v-card-text>
      Verfasser: {{dataProductRating.userName}}<br>
      Datum: {{new Date(dataProductRating.submitted).toLocaleDateString('ge-GE')}}
    </v-card-text>
  </v-card>
</template>

<script>

  export default
  {
    props:
      {
        dataProductRating:
          {
            title: String,
            comment: String,
            rating: Number,
            userName: String,
            submitted: Date
          }
      }
  }
</script>