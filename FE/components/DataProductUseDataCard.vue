<template>
  <v-card>
    <v-container>
      <v-row>
        <v-col cols="12" md="6">
          <v-row>
            <v-col cols="12" md="6" class="py-1">
              <v-btn class="ml-4" outlined>
                <a
                    style="color: white; text-decoration: unset"
                    target="_blank"
                    :href="'/api/Gateway/DataProduct/' + shortKey + '/Data'"
                >RESTful API</a
                >
              </v-btn>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="6" class="py-1">
              <v-btn class="ml-4" outlined>
                <a
                    style="color: white; text-decoration: unset"
                    target="_blank"
                    :href="'dataTableView?shortKey=' + shortKey"
                >Vorschau (in Tabelle)</a
                >
              </v-btn>
            </v-col>
          </v-row>
        </v-col>
        <v-col cols="12" md="6">
          <v-layout row align-center>
            <v-img src="insights.png" contain max-width="80px" class="mx-4" />

            <v-btn class="mx-4" outlined>
              <a
                  style="color: white; text-decoration: unset"
                  target="_blank"
                  :href="'dataTableView?shortKey=' + shortKey"
              >Insights</a
              >
            </v-btn>
          </v-layout>
        </v-col>
      </v-row>
    </v-container>

    <v-layout justify-center>
      <v-btn class="mt-2" @click="$emit('on-close-dialog')"> Zurück </v-btn>
    </v-layout>
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