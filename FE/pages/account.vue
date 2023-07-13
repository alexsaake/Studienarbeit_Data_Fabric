<template>
  <v-form v-if="$auth.loggedIn">
    <h1>User details</h1>
    <v-text-field v-model="$auth.user.firstName" type="text" class="form-control" label="First Name" disabled></v-text-field>
    <v-text-field v-model="$auth.user.lastName" type="text" class="form-control" label="Last Name" disabled></v-text-field>
    <v-text-field v-model="$auth.user.userName" type="text" class="form-control" label="User Name" disabled></v-text-field>
    <v-text-field v-model="$auth.user.email" type="email" class="form-control" label="Email" disabled></v-text-field>
  </v-form>
  <v-card-text v-else>No user logged in!</v-card-text>
</template>