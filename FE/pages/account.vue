<template>
  <v-card v-if="$auth.loggedIn">
    <v-container>
    <v-form>
      <h1>Benutzerdetails</h1>
      <v-text-field v-model="user.firstName" type="text" class="form-control" label="Vorname" :disabled="!isEditing" />
      <v-text-field v-model="user.lastName" type="text" class="form-control" label="Nachname" :disabled="!isEditing" />
      <v-text-field v-model="user.userName" type="text" class="form-control" label="Benutzername" disabled />
      <v-text-field v-model="user.email" type="email" class="form-control" label="Email" :disabled="!isEditing" />
    </v-form>
    </v-container>
    <v-container>
      <v-card-actions>
        <v-btn @click="onLogout()">Ausloggen</v-btn>
        <v-btn @click="onEdit()">{{ editSaveButtonText }}</v-btn>
      </v-card-actions>
        <h1>Deine Bewertungen</h1>
        <v-row v-if="userRatings.length==0" justify="center" >Keine eigenen Bewertungen gefunden</v-row>
        <v-row v-else-if="userRatings.length !=0" class="pb-10" no-gutters>

          <v-col v-for="(rating, index) in userRatings" :key="index" cols="12">
            <v-lazy :min-height="200" :options="{'threshold':0.5}" transition="fade-transition">
              <user-rating-card
                  :data-product-id="rating.dataProductId"
                  :rating-id="rating.id"
              />
            </v-lazy>
          </v-col>
        </v-row>
    </v-container>
  </v-card>
  <v-card-text v-else>Kein Benutzer eingeloggt!</v-card-text>
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
        editSaveButtonText: 'Bearbeiten'
      }
    },
    async fetch() {
      this.user.firstName = this.$auth.user.firstName;
      this.user.lastName = this.$auth.user.lastName;
      this.user.userName = this.$auth.user.userName;
      this.user.email = this.$auth.user.email;
      await this.fetchUserRatings();
    },
    methods: {
      async fetchUserRatings() {
        const userRatings = await getUserRatings(this.$axios);
        const formattedUserRatings = [];
        for (const rawRating of userRatings) {
          const formattedRating = {
            dataProductId: rawRating.dataProductId,
            id: rawRating.id
          };
          formattedUserRatings.push(formattedRating);
        }
        this.userRatings = formattedUserRatings;
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