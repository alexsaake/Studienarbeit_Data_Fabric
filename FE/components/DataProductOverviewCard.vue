<template>
  <v-card>
    <v-img :src="'data:text/plain;base64,' + image" height="100px" />
    <v-card-title style="word-break: break-word">{{ title }}</v-card-title>
    <v-card-subtitle>{{ shortDescription }}</v-card-subtitle>
    <v-card-text>
      Zuletzt aktualisiert: {{ lastUpdated }}<br>
      Zugriff: {{ accessRight }}
    </v-card-text>
  </v-card>
</template>

<script>
  export default
  {
    name: 'DataProductOverviewCard',
    props:
    {
      image: String,
      title: String,
      shortDescription: String,
      lastUpdated: String,
      accessRight: String
    }
  }
</script>
