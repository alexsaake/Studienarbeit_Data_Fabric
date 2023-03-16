<template>
  <v-card>
    <v-img :src="'data:text/plain;base64,' + dataProductOverview.image" height="100px" />
    <v-card-title style="word-break: break-word">{{dataProductOverview.title}}</v-card-title>
    <v-card-subtitle>{{ dataProductOverview.shortDescription }}</v-card-subtitle>
    <v-card-text>
      Zuletzt aktualisiert: {{ dataProductOverview.lastUpdated }}<br>
      Zugriff: {{ dataProductOverview.accessRight }}
    </v-card-text>
  </v-card>
</template>

<script>
  export default
  {
    props:
    {
      dataProductOverview:
      {
        shortKey: String,
        title: String,
        shortDescription: String,
        lastUpdated: Date,
        category: String,
        accessRight: String,
        image: Uint8Array
      }
    }
  }
</script>
