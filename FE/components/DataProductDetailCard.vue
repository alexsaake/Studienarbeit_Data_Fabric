<template>
  <v-card>
    <v-img :src="'data:text/plain;base64,' + image" height="300px" />
    <v-card-title style="word-break: break-word">{{ title }}</v-card-title>
    <v-card-subtitle>{{ shortDescription }}</v-card-subtitle>
    <v-card-text>
      {{ description }}<br />
      Quelle: {{ source }}<br />
      Quellen-Link: {{ sourceLink }}
      Zuletzt aktualisiert: {{ lastUpdated }}<br />
      Kategorie: {{ category }}<br />
      Zugriff: {{ accessRight }}
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
      accessRight: String
    }
  }
</script>