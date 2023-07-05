<template>
  <v-form v-if="!$auth.loggedIn" v-model="form" @submit.prevent="onSubmit">
    <h1>Please sign in</h1>
    <v-text-field v-model="userName" type="text" class="form-control" label="User Name" :rules="[required]" clearable @input="clearError"></v-text-field>
    <v-text-field v-model="password" type="password" class="form-control" label="Password" :rules="[required]" clearable @input="clearError"></v-text-field>
    <v-btn :disabled="!form" type="submit">Sign in</v-btn>
    <v-card-text class="red--text">{{error}}</v-card-text>
  </v-form>
  <v-card-text v-else>Already logged in!</v-card-text>
</template>

<script>
  export default {
    name: "Login",
    data() {
      return {
        userName: '',
        password: '',
        form: false,
        error: ''
      }
    },
    methods: {
      async onSubmit() {
        await this.$auth.loginWith("local", {data: {userName: this.userName, password: this.password}})
          .then(() => {
            window.location.reload();
          })
          .catch(() => {
            this.error = 'Username or password invalid.';
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