<template>
  <v-card class="dataProductInsights">
    <v-card v-if="!dataProductInsights || !dataProductInsightsCities || !dataProductInsightsPostalCodes">
      <v-progress-circular
        :size="120"
        indeterminate
        color="white"
      ></v-progress-circular>
    </v-card>
    <v-card v-else>
      <v-container>
        <v-row>
          <v-col cols="12" md="6">
            <v-card-subtitle>Detaillierte Informationen zum Datenprodukt</v-card-subtitle>
            <v-card-text>
              <v-container>
                <v-row class="insights-row">
                  <v-col cols="9">Anzahl an aktiven Einträgen:</v-col>
                  <v-col cols="3">{{dataProductInsights.activeItemsCount}}</v-col>
                </v-row>
                <v-row class="insights-row">
                  <v-col cols="9">Durchschnittliche Miete:</v-col>
                  <v-col cols="3">{{dataProductInsights.averageRent}} €</v-col>
                </v-row>
                <v-row class="insights-row">
                  <v-col cols="9">Durchschnittliche Zimmergröße:</v-col>
                  <v-col cols="3">{{dataProductInsights.averageSize}} m²</v-col>
                </v-row>
                <v-row class="insights-row">
                  <v-col cols="9">Günstigste Miete:</v-col>
                  <v-col cols="3">{{dataProductInsights.lowestRent}} €</v-col>
                </v-row>
                <v-row class="insights-row">
                  <v-col cols="9">Teuerste Miete:</v-col>
                  <v-col cols="3">{{dataProductInsights.highestRent}} €</v-col>
                </v-row>
                <v-row class="insights-row">
                  <v-col cols="9">Miete Median:</v-col>
                  <v-col cols="3">{{dataProductInsights.medianRent}} €</v-col>
                </v-row>
                <v-row class="insights-row">
                  <v-col cols="9">Miete 25% Quartil:</v-col>
                  <v-col cols="3">{{dataProductInsights.quartile25Rent}} €</v-col>
                </v-row>
                <v-row class="insights-row">
                  <v-col cols="9">Miete 75% Quartil:</v-col>
                  <v-col cols="3">{{dataProductInsights.quartile75Rent}} €</v-col>
                </v-row>
              </v-container>
            </v-card-text>
            <v-container>
              <v-row class="insights-row">
                <v-select
                  v-model="newEvent.city"
                  style="height: 80px"
                  clearable
                  chips
                  label="Stadt"
                  :items="dataProductInsightsCities.cities"
                  multiple
                  @input="reloadData = true; /* newEvent.postalCodes = ['Alle']; */"
                ></v-select>
              </v-row>
              <v-row class="insights-row">
                <v-select
                  v-model="newEvent.postalCodes"
                  clearable
                  chips
                  style="height: 80px"
                  label="Bezirk"
                  :items="dataProductInsightsPostalCodes.postalCodes"
                  multiple
                  @input="reloadData = true"

                ></v-select>
              </v-row>
              <v-row class="insights-row">
                <v-col cols="12" md="6">
                  <v-menu
                    v-model="WhenStartedDate"
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="290px"
                  >
                    <template #activator="{ on, attrs }">
                      <v-text-field
                        v-model="newEvent.whenStartedDate"
                        style="height: 60px"
                        label="Datum VON"
                        :prepend-icon="dateIcon"
                        :append-icon="(newEvent.whenStartedDate === null ? undefined : closeIcon)"
                        readonly
                        v-bind="attrs"
                        @click:prepend="WhenStartedDate = true"
                        @click:append="newEvent.whenStartedDate = null;reloadData = true"
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker v-model="newEvent.whenStartedDate" class="includedPopout" @input="WhenStartedDate = false;reloadData = true" />
                  </v-menu>
                </v-col>
                <v-col cols="12" md="6">
                  <v-menu
                    v-model="WhenEndedDate"
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="290px"
                  >
                    <template #activator="{ on, attrs }">

                      <v-text-field
                        v-model="newEvent.whenEndedDate"
                        style="height: 60px"
                        label="Datum BIS"
                        :prepend-icon="dateIcon"
                        :append-icon="(newEvent.whenEndedDate === null ? undefined : closeIcon)"
                        readonly
                        v-bind="attrs"
                        @click:prepend="WhenEndedDate = true"
                        @click:append="newEvent.whenEndedDate = null;reloadData = true"
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker v-model="newEvent.whenEndedDate" class="includedPopout" @input="WhenEndedDate = false;reloadData = true" />
                  </v-menu>
                </v-col>
              </v-row>
            </v-container>
          </v-col>
          <v-col cols="12" md="6" >
            <insights-map-card :short-key="shortKey" :maps-data="dataProductInsights.mapsData"/>
          </v-col>
        </v-row>
      </v-container>
    </v-card>
  </v-card>
</template>

<script>
import { mdiCalendarRange, mdiClose} from '@mdi/js'
import {
  getDataProductInsights, getDataProductInsightsCities, getDataProductInsightsPostalCodes
} from "~/middleware/dataProductService";

export default {
  props: {
    shortKey: {
      type: String,
      required: false,
      default: '',
    }
  },
  data() {
    return {
      dataProductInsights: null,
      dataProductInsightsCities: null,
      dataProductInsightsPostalCodes: null,
      dateIcon: mdiCalendarRange,
      closeIcon: mdiClose,
      date: new Date().toISOString().substr(0, 10),
      menu1: false,
      menu2: false,
      WhenStartedDate: null,
      WhenEndedDate: null,
      newEvent: {
        whenStartedDate: null,
        whenEndedDate: null,
        city: null,// ['Alle'],
        postalCodes: null// ['Alle']
      },
      reloadData: false
    }
  },
  async fetch() {
    await this.loadInsights();
    await this.loadCities();
    await this.loadPostalCodes();
  },
  computed: {
    computedDateFormatted () {
      return this.formatDate(this.date)
    },
  },
  watch: {
    shortKey() {
      this.dataProductInsights = null // Reset to null when shortKey changes to indicate data is not yet loaded
    },
    async dataProductInsights() {
      // Load data when dataProductDetail changes from null to an object
      if (this.dataProductInsights === null) {
        this.reloadData = false;
        await this.loadInsights();
      }
    },
    async dataProductInsightsCities() {
      // Load data when dataProductDetail changes from null to an object
      if (this.dataProductInsightsCities === null) {
        await this.loadCities();
      }
    },
    async dataProductInsightsPostalCodes() {
      // Load data when dataProductDetail changes from null to an object
      if (this.dataProductInsightsPostalCodes === null) {
        await this.loadPostalCodes();
      }
    },
    date (val) {
      this.dateFormatted = this.formatDate(this.date);
    },
    async reloadData(){
      this.reloadData = false;
      await this.loadInsights();
      await this.loadPostalCodes();
    },
    WhenEndedDate(){
      sessionStorage.setItem("datePickerOpen", this.WhenEndedDate);
    },
    WhenStartedDate(){
      sessionStorage.setItem("datePickerOpen", this.WhenStartedDate);
    }
  },
  fetchOnServer: false,
  methods: {
    async fetchDataProductInsights(shortKey) {
      const rawDataProductInsights = await getDataProductInsights(
        this.$axios,
        shortKey,
        {
          areaFilter: this.parseSelectData(this.newEvent.city),
          areaFilter2: this.parseSelectData(this.newEvent.postalCodes),
          dateFromFilter: this.newEvent.whenStartedDate,
          dateToFilter: this.newEvent.whenEndedDate,
        }
      );
      return {
        averageRent: rawDataProductInsights.averageRent,
        activeItemsCount: rawDataProductInsights.activeItemsCount,
        averageSize: rawDataProductInsights.averageSize,
        highestRent: rawDataProductInsights.highestRent,
        lowestRent: rawDataProductInsights.lowestRent,
        medianRent: rawDataProductInsights.medianRent,
        quartile25Rent: rawDataProductInsights.quartile25Rent,
        quartile75Rent: rawDataProductInsights.quartile75Rent,
        mapsData: rawDataProductInsights.mapsData,
      };
    },
    async fetchDataProductInsightsCities(shortKey) {
      const rawDataProductInsightsCities = await getDataProductInsightsCities(
        this.$axios,
        shortKey);
      return {
        cities: rawDataProductInsightsCities
      };
    },
    async fetchDataProductInsightsPostalCodes(shortKey) {
      const rawDataProductInsightsPostalCodes = await getDataProductInsightsPostalCodes(
        this.$axios,
        shortKey,
        {
          areaFilter: this.parseSelectData(this.newEvent.city)
        });
      return {
        postalCodes: rawDataProductInsightsPostalCodes
      };
    },
    async loadInsights(){
      this.dataProductInsights = await this.fetchDataProductInsights(
        this.shortKey
      )
    },
    async loadCities(){
      const array = await this.fetchDataProductInsightsCities(
        this.shortKey
      );
      // (array.cities != null?array.cities.unshift('Alle'):array.cities = ['Alle']);
      this.dataProductInsightsCities = array;
    },
    async loadPostalCodes(){
      const array = await this.fetchDataProductInsightsPostalCodes(
        this.shortKey
      );
      // (array.postalCodes != null?array.postalCodes.unshift('Alle'):array.postalCodes = ['Alle']);
      this.dataProductInsightsPostalCodes = array;
    },
    formatDate (date) {
      if (!date) return null

      const [year, month, day] = date.split('-')
      return `${month}/${day}/${year}`
    },
    parseDate (date) {
      if (!date) return null

      const [month, day, year] = date.split('/')
      return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`
    },
    parseSelectData(data){
      if(data == null || data.length < 1)
        return null;
      // if(data.includes("Alle") && data.length === 1){
      //   return null;
      // }
      // if(data.includes("Alle") && data[data.length-1] === "Alle"){
      //   return data.pop();
      // }
      // if(data.includes("Alle")){
      //   return data.shift();
      // }
      return data.join(",");
    }
  },
}
</script>

<style lang="css">
  .insights-row div{
      padding: 4px;
  }
  .v-input__prepend-outer{
      margin-right: 0;
      margin-top: 15px;
  }
  .dataProductDetail {
    width: 50%;
    transform: translate(50%, 0);
  }
  /*@media screen and (max-width: 900px) {*/
  /*    .dataProductDetail {*/
  /*        width: 70%;*/
  /*        transform: translate(30%, 0);*/
  /*    }*/
  /*}*/
  @media screen and (max-width: 600px) {
      .dataProductDetail {
          width: 100%;
          transform: unset;
      }
  }
</style>
