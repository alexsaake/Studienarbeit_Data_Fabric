<template>
  <v-card class="dataProductInsights">
    <v-card v-if="!dataProductInsights || !dataProductInsightsCities">
      <v-progress-circular
        :size="120"
        indeterminate
        color="white"
      ></v-progress-circular>
    </v-card>
    <v-card v-else>
      <v-card-title style="word-break: break-word">Insights</v-card-title>
      <v-card-subtitle>Detaillierte Informationen zum Datenprodukt</v-card-subtitle>
      <v-card-text>
        <v-container>
          <v-row class="insights-row">
            <v-col cols="12" md="8">Anzahl an aktiven Einträgen:</v-col>
            <v-col cols="12" md="4">{{dataProductInsights.activeItemsCount}}</v-col>
          </v-row>
          <v-row class="insights-row">
            <v-col cols="12" md="8">Durchschnittliche Miete:</v-col>
            <v-col cols="12" md="4">{{dataProductInsights.averageRent}} €</v-col>
          </v-row>
          <v-row class="insights-row">
            <v-col cols="12" md="8">Durchschnittliche Zimmergröße:</v-col>
            <v-col cols="12" md="4">{{dataProductInsights.averageSize}} m²</v-col>
          </v-row>
          <v-row class="insights-row">
            <v-col cols="12" md="8">Günstigste Miete:</v-col>
            <v-col cols="12" md="4">{{dataProductInsights.lowestRent}} €</v-col>
          </v-row>
          <v-row class="insights-row">
            <v-col cols="12" md="8">Teuerste Miete:</v-col>
            <v-col cols="12" md="4">{{dataProductInsights.highestRent}} €</v-col>
          </v-row>
          <v-row class="insights-row">
            <v-col cols="12" md="8">Miete Median:</v-col>
            <v-col cols="12" md="4">{{dataProductInsights.medianRent}} €</v-col>
          </v-row>
          <v-row class="insights-row">
            <v-col cols="12" md="8">Miete 25% Quartil:</v-col>
            <v-col cols="12" md="4">{{dataProductInsights.quartile25Rent}} €</v-col>
          </v-row>
          <v-row class="insights-row">
            <v-col cols="12" md="8">Miete 75% Quartil:</v-col>
            <v-col cols="12" md="4">{{dataProductInsights.quartile75Rent}} €</v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-container>
        <v-row class="insights-row">
            <v-combobox
              v-model="newEvent.city"
              class="combobox includedPopout"
              style="height: 60px"
              label="Stadt"
              :items="dataProductInsightsCities.cities"
              @input="reloadData = true"
            ></v-combobox>
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
    </v-card>
  </v-card>
</template>

<script>
import { mdiCalendarRange, mdiClose} from '@mdi/js'
import {
  getDataProductInsights, getDataProductInsightsCities
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
        city: 'Alle'
      },
      reloadData: false
    }
  },
  async fetch() {
    this.dataProductInsights = await this.fetchDataProductInsights(this.shortKey);
    const array = await this.fetchDataProductInsightsCities(
      this.shortKey
    )
    array.cities.unshift('Alle');
    this.dataProductInsightsCities = array;
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
        this.dataProductInsights = await this.fetchDataProductInsights(
          this.shortKey
        )
      }
    },
    async dataProductInsightsCities() {
      // Load data when dataProductDetail changes from null to an object
      if (this.dataProductInsightsCities === null) {
        const array = await this.fetchDataProductInsightsCities(
          this.shortKey
        )
        array.cities.unshift('Alle');
        this.dataProductInsightsCities = array;
      }
    },
    date (val) {
      this.dateFormatted = this.formatDate(this.date);
    },
    async reloadData(){
      this.reloadData = false;
      this.dataProductInsights = await this.fetchDataProductInsights(
        this.shortKey
      )
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
          areaFilter: (this.newEvent.city==="Alle"?null:this.newEvent.city),
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
</style>
