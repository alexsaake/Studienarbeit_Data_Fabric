<template>
  <v-card>
    <v-card-title v-if="dataProductRating.title !== null" style="word-break: break-word">{{dataProductRating.title}}</v-card-title>
    <v-card-text v-if="dataProductRating.comment !== null" class="text-pre-wrap">{{dataProductRating.comment}}</v-card-text>
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