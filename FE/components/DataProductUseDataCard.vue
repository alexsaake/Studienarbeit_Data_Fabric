<template>
  <v-card>
    <v-container>
      <v-row>
        <v-col cols="12" md="12" style="padding-bottom: 0">
          <h3>Daten</h3>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="6">
          <v-card :to="'/api/Gateway/DataProduct/' + id + '/Data'" target="_blank" class="d-flex flex-column fill-height">
            <v-card-subtitle>RESTful API</v-card-subtitle>
          </v-card>
        </v-col>
        <v-col cols="12" md="6">
          <v-card :to="'/dataTableView?id=' + id" target="_blank" class="d-flex flex-column fill-height">
            <v-card-subtitle>Vorschau (in Tabelle)</v-card-subtitle>
          </v-card>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="12" style="padding-bottom: 0">
          <h3>Insights</h3>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="12">
          <data-product-insights-card :id="id" />
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
      id: {
        type: Number,
        required: false,
        default: -1
      }
    }
  }
</script>


<style lang="css">
  .my-overlay >>> .v-overlay__content  {
    width: 50%;
    height: 100%;
    overflow-y: scroll;
  }

</style>