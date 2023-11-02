<template>
  <v-card v-if="$auth.loggedIn">
    <v-container>
    <v-form>
      <h1>User details</h1>
      <v-text-field v-model="user.firstName" type="text" class="form-control" label="First Name" :disabled="!isEditing" />
      <v-text-field v-model="user.lastName" type="text" class="form-control" label="Last Name" :disabled="!isEditing" />
      <v-text-field v-model="user.userName" type="text" class="form-control" label="User Name" disabled />
      <v-text-field v-model="user.email" type="email" class="form-control" label="Email" :disabled="!isEditing" />
    </v-form>
    </v-container>
    <v-container>


      <v-card-actions>
        <v-btn @click="onLogout()">Logout</v-btn>
        <v-btn @click="onEdit()">{{ editSaveButtonText }}</v-btn>
      </v-card-actions>

        <v-row v-if="userRatings.length !=0" justify="center" class="pb-10">Your Ratings</v-row>
        <v-row no-gutters>
          <v-col v-for="(rating, index) in userRatings" :key="index" cols="12">
            <user-rating-card
                :id="rating.id"
                :title="rating.title"
                :comment="rating.comment"
                :rating="rating.rating"
                :submitted="rating.submitted"
                :is-edited="rating.isEdited"
            />
          </v-col>
          <v-row v-if="userRatings.length==0" justify="center" >Keine eigenen Bewertungen gefunden</v-row>
        </v-row>
    </v-container>
  </v-card>
  <v-card-text v-else>No user logged in!</v-card-text>
</template>

<script>
import { updateUser, getUserRatings } from "~/middleware/userService";

  export default {
    name: 'Account',
    data() {
      return {
        user: {
          firstName: '',
          lastName: '',
          userName: '',
          email: ''
        },
        userRatings: [],
        isEditing: false,
        editSaveButtonText: 'Edit'
      }
    },
    async fetch() {
      this.user.firstName = this.$auth.user.firstName;
      this.user.lastName = this.$auth.user.lastName;
      this.user.userName = this.$auth.user.userName;
      this.user.email = this.$auth.user.email;
      this.userRatings = await this.fetchUserRatings();
    },
    methods: {
      async fetchUserRatings() {
        const userRatings = await getUserRatings(this.$axios);
        const formattedUserRatings = [];
        for (const rawRating of userRatings) {
          const formattedRating = {
            id: rawRating.id,
            title: rawRating.title,
            comment: rawRating.comment,
            rating: rawRating.rating,
            submitted: new Date(rawRating.submitted).toLocaleDateString('ge-GE'),
            isEdited: rawRating.edited
          };
          formattedUserRatings.push(formattedRating);
        }
        return formattedUserRatings;
      },

      async onLogout() {
        await this.$auth.logout();
      },
      async onEdit() {
        if(this.isEditing) {
          if(this.user.firstName !== this.$auth.user.firstName ||
          this.user.lastName !== this.$auth.user.lastName ||
          this.user.email !== this.$auth.user.email) {
            await updateUser(this.$axios, this.user.firstName, this.user.lastName, this.user.email);
            await this.$auth.fetchUser();
          }
          this.editSaveButtonText = 'Edit';
          this.isEditing = false;
        }
        else {
          this.editSaveButtonText = 'Save';
          this.isEditing = true;
        }
      }
    }
  }
</script>