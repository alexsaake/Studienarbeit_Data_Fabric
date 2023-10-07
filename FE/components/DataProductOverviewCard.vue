<template>
  <v-card class="d-flex flex-column">
    <v-img :src="'data:text/plain;base64,' + image" height="100px" />
    <v-card-title style="word-break: break-word">{{ title }}</v-card-title>
    <v-card-subtitle>{{ shortDescription }}</v-card-subtitle>
    <v-spacer />
    <v-card-text>
      <v-container class="pa-0">
        <v-row no-gutters>
          <v-col>Zuletzt aktualisiert:</v-col>
          <v-col>{{ lastUpdated.toLocaleDateString('ge-GE') }}</v-col>
        </v-row>
        <v-row class="mt-4" no-gutters>
          <v-col>Zugriff</v-col>
          <v-col>{{ accessRight }}</v-col>
        </v-row>
        <v-row class="mt-4" no-gutters>
          <v-col>Durchschnittliche Bewertung</v-col>
          <v-col><pre>{{ averageRating.toFixed(2) }}</pre></v-col>
          <v-col><v-rating :value="averageRating" readonly half-increments></v-rating></v-col>
        </v-row>
      </v-container>
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
      lastUpdated: Date,
      accessRight: String,
      averageRating: Number
    }
  }
</script>
