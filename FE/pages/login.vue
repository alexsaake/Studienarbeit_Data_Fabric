<template>
  <v-form v-if="!$auth.loggedIn" v-model="form" @submit.prevent="onSubmit">
    <h1>Anmeldung</h1>
    <v-text-field v-model="userName" type="text" class="form-control" label="Benutzername" :rules="[required]" clearable @input="clearError"></v-text-field>
    <v-text-field v-model="password" type="password" class="form-control" label="Passwort" :rules="[required]" clearable @input="clearError"></v-text-field>
    <v-btn :disabled="!form" type="submit">Anmelden</v-btn>
    <v-card-text class="red--text">{{error}}</v-card-text>
  </v-form>
  <v-card-text v-else>Bereits eingeloggt!</v-card-text>
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
        await this.$auth.loginWith('local', {data: {userName: this.userName, password: this.password}})
          .then(() => {
            if(this.$route.query.page !== undefined)
              this.$router.push('/'+ this.$route.query.page );
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