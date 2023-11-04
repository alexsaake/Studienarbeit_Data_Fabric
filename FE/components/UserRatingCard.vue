<template>
  <v-card  class="my-card" @click="navigateToMarketplace">
    <v-card v-if="isLoading">
      <v-progress-circular :size="120" indeterminate color="white"/>
    </v-card>
    <v-card v-else>
      <v-card-title v-if="title !== null" style="word-break: break-word">{{dataProductTitle}}</v-card-title>
      <v-rating :value="rating" readonly></v-rating>
      <v-card-text>
        <v-container class="pa-0">
          <v-row no-gutters>
            <v-col>
              <v-row v-if="title !== null" class="rating-card-title" no-gutters>
                <v-col>
                  {{ title }}
                </v-col>
              </v-row>
              <v-row v-if="comment !== null" class="rating-card-comment" no-gutters>
                <v-col>
                  {{ comment }}
                </v-col>
              </v-row>
            </v-col>
          </v-row>
          <v-row class="mt-4" no-gutters>
            <v-col>Datum: {{ submitted.toLocaleDateString('ge-GE') }}</v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-text v-show="isEdited">
        bearbeitet
      </v-card-text>
    </v-card>
  </v-card>
</template>

<script>
import {getDataProduct, getDataProductRating} from "~/middleware/dataProductService";

export default
{
  name: 'UserRatingCard',
  props:
      {
        dataProductId: Number,
        ratingId: Number,
      },
  data()
  {
    return {
      dataProductTitle: '',
      title: '',
      comment: '',
      rating: 0,
      submitted: new Date(),
      isEdited: false,
      isLoading: true
    }
  },
  async fetch() {
    this.dataProductTitle = await this.getDataProductTitle(this.dataProductId);
    await this.fetchRating();
  },
  methods:
      {
        async fetchRating() {
          const rawRating = await getDataProductRating(this.$axios, this.ratingId);
          this.title = rawRating.title;
          this.comment = rawRating.comment;
          this.rating = rawRating.rating;
          this.submitted = new Date(rawRating.submitted);
          this.isEdited = rawRating.isEdited;

          this.isLoading = false;
        },
        async getDataProductTitle(id) {
          const dataProductTitle = await getDataProduct(this.$axios, id);
          return dataProductTitle.title;
        },
        navigateToMarketplace() {
          this.$router.push({ path: '/marketplace', query: { id: this.dataProductId } });
        },
      }
}
</script>

<style scoped>
.my-card
{
  position: relative;
}
.rating-card-title {
  font-size: 18px; /* Adjust the font size as needed */
  font-weight: bold;
  margin-bottom: 8px; /* Add some spacing between title and comment */
}
.rating-card-comment {
  font-size: 14px; /* Adjust the font size as needed */
}
</style>