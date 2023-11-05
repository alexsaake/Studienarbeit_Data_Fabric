<template>
  <v-form v-if="!$auth.loggedIn" v-model="form" @submit.prevent="onSubmit">
    <h1>Please register</h1>
    <v-text-field v-model="firstName" type="text" class="form-control" label="First Name" :rules="[required]" clearable></v-text-field>
    <v-text-field v-model="lastName" type="text" class="form-control" label="Last Name" :rules="[required]" clearable></v-text-field>
    <v-text-field v-model="userName" type="text" class="form-control" label="User Name" :rules="[required]" clearable @input="clearError"></v-text-field>
    <v-text-field v-model="email" type="email" class="form-control" label="Email" :rules="[required]" clearable @input="clearError"></v-text-field>
    <v-text-field v-model="password" type="password" class="form-control" label="Password" :rules="[required]" clearable></v-text-field>
    <v-btn :disabled="!form" type="submit">Submit</v-btn>
    <v-card-text class="red--text">{{error}}</v-card-text>
  </v-form>
  <v-card-text v-else>Already logged in!</v-card-text>
</template>

<script>
  import {registerUser} from "~/middleware/authService";

  export default {
    name: "Register",
    data() {
      return {
        firstName: '',
        lastName: '',
        userName: '',
        email: '',
        password: '',
        form: false,
        error: ''
      }
    },
    methods: {
      async onSubmit() {
        await registerUser(this.$axios, this.firstName, this.lastName, this.userName, this.email, this.password)
          .then(async response => {
            if(!response) {
              this.error = 'Username or email already taken.';
              return;
            }
            await this.$auth.loginWith('local', {data: {userName: this.userName, password: this.password}})
          })
          .catch(() => {
            this.error = 'Username or email already taken.';
          });
      },
      required (v) {
        return !!v || 'Field is required'
      },
      clearError(){
        this.error = '';
      }
    }
  }
</script>