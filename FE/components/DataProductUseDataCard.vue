<template>
  <v-card>
    <v-container>
      <v-row>
        <v-col cols="6" md="3">
          <v-card :to="'/api/Gateway/DataProduct/' + shortKey + '/Data'" target="_blank" class="d-flex flex-column fill-height">
            <v-img src="insights.png"/>
            <v-card-title>RESTful API</v-card-title>
          </v-card>
        </v-col>
        <v-col cols="6" md="3">
          <v-card :to="'/dataTableView?shortKey=' + shortKey" target="_blank" class="d-flex flex-column fill-height">
            <v-img src="insights.png"/>
            <v-card-title>Vorschau</v-card-title>
            <v-card-subtitle>(in Tabelle)</v-card-subtitle>
          </v-card>
        </v-col>
        <v-col cols="6" md="3">
          <v-card :to="'/dataTableView?shortKey=' + shortKey" target="_blank" class="d-flex flex-column fill-height">
            <v-img src="insights.png"/>
            <v-card-title>Insights</v-card-title>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    <v-card-actions>
      <v-btn @click="$emit('on-close-dialog')">Zurück</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
  export default {
    name: 'DataProductUseDataCard',
    props: {
      shortKey: {
        type: String,
        required: false,
        default: ''
      }
    }
  }
</script>