<template>
  <v-card class="dataProductInsights">
    <v-card v-if="!dataProductInsights || !dataProductInsightFilters">
      <v-progress-circular
        :size="120"
        indeterminate
        color="white"
      ></v-progress-circular>
    </v-card>
    <v-card v-else>
      <v-container>
        <v-row>
          <v-col v-if="dataProductInsights.insightData === undefined || dataProductInsights.insightData.length <= 0" cols="12">
            <v-card-subtitle>Keine Insights hinterlegt</v-card-subtitle>
          </v-col>
          <v-col v-else cols="12" :md="(dataProductInsights.mapsData === undefined || dataProductInsights.mapsData.length <= 0?12:6)">
            <v-card-subtitle>Detaillierte Informationen zum Datenprodukt</v-card-subtitle>
            <v-card-text>
              <v-container>
                <v-row class="insights-row">
                  <v-col style="font-weight: bold" cols="9">Anzahl betrachteter Datensätze:</v-col>
                  <v-col  style="font-weight: bold" cols="3">{{dataProductInsights.insightCount}}</v-col>
                </v-row>
              </v-container>
              <v-container>
                <v-row v-for="data in dataProductInsights.insightData" :key="data.displayName" class="insights-row">
                  <v-col cols="9">{{data.displayName}}:</v-col>
                  <v-col cols="3">{{data.insightValue}} {{data.unit}}</v-col>
                </v-row>
              </v-container>
            </v-card-text>
            <v-container v-for="(data) in dataProductInsightFilters.filters" :key="data.displayName">
              <v-row v-if="data.filterType === 3" class="insights-row">
                <v-select
                  v-model="getFilterById(data.filterId).value"
                  style="height: 80px"
                  clearable
                  chips
                  :label="data.displayName"
                  :items="getFilterById(data.filterId).filterValues"
                  multiple
                  @input="reloadData = true;"
                ></v-select>
              </v-row>
              <v-row v-if="data.filterType === 2" class="insights-row">
                <v-select
                  v-model="getFilterById(data.filterId).value"
                  style="height: 80px"
                  clearable
                  chips
                  :label="data.displayName"
                  :items="getFilterById(data.filterId).filterValues"
                  multiple
                  @input="reloadData = true;"
                ></v-select>
              </v-row>
              <v-row v-if="data.filterType === 1" class="insights-row">
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
                        v-model="getFilterById(data.filterId).dateFrom"
                        style="height: 60px"
                        :label="data.displayName+' von'"
                        :prepend-icon="dateIcon"
                        :append-icon="(getFilterById(data.filterId).dateFrom === null ? undefined : closeIcon)"
                        readonly
                        v-bind="attrs"
                        @click:prepend="WhenStartedDate = true"
                        @click:append="getFilterById(data.filterId).dateFrom = null;reloadData = true"
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker v-model="getFilterById(data.filterId).dateFrom" class="includedPopout" @input="WhenStartedDate = false;reloadData = true" />
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
                        v-model="getFilterById(data.filterId).dateTo"
                        style="height: 60px"
                        :label="data.displayName+' bis'"
                        :prepend-icon="dateIcon"
                        :append-icon="(getFilterById(data.filterId).dateTo === null ? undefined : closeIcon)"
                        readonly
                        v-bind="attrs"
                        @click:prepend="WhenEndedDate = true"
                        @click:append="getFilterById(data.filterId).dateTo = null;reloadData = true"
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker v-model="getFilterById(data.filterId).dateTo" class="includedPopout" @input="WhenEndedDate = false;reloadData = true" />
                  </v-menu>
                </v-col>
              </v-row>
            </v-container>
          </v-col>
          <v-col  v-if="dataProductInsights.mapsData !== undefined && dataProductInsights.mapsData.length > 0" cols="12" md="6" >
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
  getDataProductInsights, getDataProductInsightFilters, getDataProductInsightFilterValues
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
      dataProductInsightFilters: null,
      dataProductInsightFilterValues: null,
      dateIcon: mdiCalendarRange,
      closeIcon: mdiClose,
      date: new Date().toISOString().substr(0, 10),
      menu1: false,
      menu2: false,
      WhenStartedDate: null,
      WhenEndedDate: null,
      filterData: [],
      newEvent: {
        whenStartedDate: null,
        whenEndedDate: null,
        city: null,
        postalCodes: null
      },
      reloadData: false
    }
  },
  async fetch() {
    await this.loadInsights();
    await this.loadFilters();
    await this.loadFilterValues();
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
    async dataProductInsightFilters() {
      // Load data when dataProductDetail changes from null to an object
      if (this.dataProductInsightFilters === null) {
        await this.loadFilters();
      }
    },
    date (val) {
      this.dateFormatted = this.formatDate(this.date);
    },
    async reloadData(){
      this.reloadData = false;
      await this.loadInsights();
      await this.loadFilterValues();
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
          filterKeys: this.setFilterKeys(),
          filterValues: this.setFilterValues()
        }
      );
      return {
        insightData: rawDataProductInsights.insightData,
        insightCount: rawDataProductInsights.insightCount,
        mapsData: rawDataProductInsights.mapsData,
      };
    },
    async fetchDataProductInsightFilters(shortKey) {
      const rawDataProductInsightFilters = await getDataProductInsightFilters(
        this.$axios,
        shortKey);
      return {
        filters: rawDataProductInsightFilters
      };
    },
    async fetchDataProductInsightFilterValues(shortKey, filterId) {
      const rawDataProductInsightFilterValues = await getDataProductInsightFilterValues(
        this.$axios,
        shortKey,
        filterId,
        {
          areaFilter: this.parseSelectData(this.newEvent.city)
        });
      return {
        filterValues: rawDataProductInsightFilterValues
      };
    },
    async loadInsights(){
      this.dataProductInsights = await this.fetchDataProductInsights(
        this.shortKey
      )
    },
    async loadFilters(){
      const array = await this.fetchDataProductInsightFilters(
        this.shortKey
      );
      this.filterData = [];
      for (const filter of array.filters) {
        const filterV = await this.loadFilterValues(filter.filterId);
        this.filterData.push({
          filterId: filter.filterId,
          filterType: filter.filterType,
          value: null,
          dateFrom: null,
          dateTo: null,
          filterValues: filterV.filterValues
        });
      }
      this.dataProductInsightFilters = array;
    },
    async loadFilterValues(filterId){
      if(filterId === undefined)
        return null;
      return await this.fetchDataProductInsightFilterValues(
        this.shortKey,
        filterId
      );
    },
    setFilterKeys(){
      const filterKeys = [];
      this.filterData.forEach (filter =>{
        if((filter.value == null || filter.value.length <= 0) && filter.dateTo == null && filter.dateFrom == null)
          return;
        filterKeys.push(filter.filterId);
      });
      console.log(filterKeys.join(";"));
      return (filterKeys.length === 0?null:filterKeys.join(";"));
    },
    setFilterValues(){
      const filterValues = [];
      this.filterData.forEach (filter =>{
        if(filter.value != null && filter.value.length > 0)
          filterValues.push(filter.value.join(","));
        if(filter.dateTo != null || filter.dateFrom != null)
          filterValues.push(filter.dateFrom+","+filter.dateTo);
      });
      console.log(filterValues.join(";"));
      return (filterValues.length === 0?null:filterValues.join(";"));
    },
    getFilterById(id){
      let filterObj = null;
      this.filterData.forEach (filter =>{
        if(filter.filterId === id)
          filterObj = filter;
      });
      return filterObj;
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
