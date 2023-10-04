<template>
  <v-card>
    <v-img :src="'data:text/plain;base64,' + image" height="300px" />
    <v-card-actions style="position: absolute; top: 310px; right: 20px;">
      <v-btn v-show="$auth.loggedIn && $auth.user.userName === userName" icon @click="onShowConfirmDeleteRating" >
        <v-icon>mdi-delete</v-icon>
      </v-btn>
      <v-btn v-show="$auth.loggedIn && $auth.user.userName === userName" icon @click="$emit('on-edit-data-product')">
        <v-icon>mdi-lead-pencil</v-icon>
      </v-btn>
    </v-card-actions>
    <v-card-title style="word-break: break-word">{{ title }}</v-card-title>
    <v-card-subtitle>{{ shortDescription }}</v-card-subtitle>
    <v-card-text>
      <v-container class="pa-0">
        <v-row no-gutters>
          <v-col>{{ description }}</v-col>
        </v-row>
        <v-row class="mt-4" no-gutters>
          <v-col cols="2">Quelle</v-col>
          <v-col>{{ source }}</v-col>
        </v-row>
        <v-row class="mt-4" no-gutters>
          <v-col cols="2">Quellen-Link:</v-col>
          <v-col>{{ sourceLink }}</v-col>
        </v-row>
        <v-row class="mt-4" no-gutters>
          <v-col cols="2">Zuletzt aktualisiert:</v-col>
          <v-col>{{ lastUpdated }}</v-col>
        </v-row>
        <v-row class="mt-4" no-gutters>
          <v-col cols="2">Kategorie</v-col>
          <v-col>{{ category }}</v-col>
        </v-row>
        <v-row class="mt-4" no-gutters>
          <v-col cols="2">Zugriff</v-col>
          <v-col>{{ accessRight }}</v-col>
        </v-row>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script>
  export default {
    name: 'DataProductDetailCard',
    props:
    {
      image: String,
      shortKey: String,
      title: String,
      shortDescription: String,
      description: String,
      source: String,
      sourceLink: String,
      lastUpdated: String,
      category: String,
      accessRight: String,
      userName: String
    },
    methods:{
      onShowConfirmDeleteRating(){

      }
    }
  }
</script>