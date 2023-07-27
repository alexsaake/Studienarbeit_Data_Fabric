<template>
  <v-card v-if="$auth.loggedIn">
    <v-form>
      <h1>User details</h1>
      <v-text-field v-model="$auth.user.firstName" type="text" class="form-control" label="First Name" disabled></v-text-field>
      <v-text-field v-model="$auth.user.lastName" type="text" class="form-control" label="Last Name" disabled></v-text-field>
      <v-text-field v-model="$auth.user.userName" type="text" class="form-control" label="User Name" disabled></v-text-field>
      <v-text-field v-model="$auth.user.email" type="email" class="form-control" label="Email" disabled></v-text-field>
    </v-form>
    <v-btn text @click="onLogout()">Logout</v-btn>
  </v-card>
  <v-card-text v-else>No user logged in!</v-card-text>
</template>

<script>

export default {
  name: 'Account',
  methods: {
    async onLogout(){
      await this.$auth.logout()
          .then(() => {
          });
    }
  }
}
</script>