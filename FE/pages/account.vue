<template>
  <v-card v-if="$auth.loggedIn">
    <v-form>
      <h1>User details</h1>
      <v-text-field v-model="user.firstName" type="text" class="form-control" label="First Name" :disabled="!isEditing" />
      <v-text-field v-model="user.lastName" type="text" class="form-control" label="Last Name" :disabled="!isEditing" />
      <v-text-field v-model="user.userName" type="text" class="form-control" label="User Name" disabled />
      <v-text-field v-model="user.email" type="email" class="form-control" label="Email" :disabled="!isEditing" />
    </v-form>
    <v-card-actions>
      <v-btn @click="onLogout()">Logout</v-btn>
      <v-btn @click="onEdit()">{{ editSaveButtonText }}</v-btn>
    </v-card-actions>
  </v-card>
  <v-card-text v-else>No user logged in!</v-card-text>
</template>

<script>
  import {updateUser} from "~/middleware/userService";

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
        isEditing: false,
        editSaveButtonText: 'Edit'
      }
    },
    fetch() {
      this.user.firstName = this.$auth.user.firstName;
      this.user.lastName = this.$auth.user.lastName;
      this.user.userName = this.$auth.user.userName;
      this.user.email = this.$auth.user.email;
    },
    methods: {
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