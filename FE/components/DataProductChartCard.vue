<template>
  <v-card class="dataProductCharts">
    <v-card v-if="!dataProductCharts">
      <v-progress-circular
        :size="120"
        indeterminate
        color="white"
      ></v-progress-circular>
    </v-card>
    <v-card v-else>
      <div v-if="!dataProductCharts.length > 0">
        <v-card-subtitle>Keine Diagramme hinterlegt</v-card-subtitle>
      </div>
      <div v-else>
        <v-tabs
          v-model="tabs"
          bg-color="primary"
        >
          <v-tab
            v-for="chart in dataProductCharts" :key="chart.displayName"
            value="one">{{chart.displayName}}</v-tab>
        </v-tabs>
        <v-tabs-items :value="tabs">
          <v-tab-item
            v-for="chart in dataProductCharts" :key="chart.displayName">
            <canvas :id="'chart_'+chart.displayName"></canvas>
          </v-tab-item>
        </v-tabs-items>
      </div>

    </v-card>
  </v-card>
</template>
<script>
import Chart from 'chart.js';
import {
  getDataProductCharts
} from "~/middleware/dataProductService";


export default {
  props: {
    id: {
      type: Number,
      required: false,
      default: -1,
    }
  },
  data() {
    return {
      dataProductCharts: null,
      tabs: null,
      charts: [],
    }
  },
  async fetch() {
    await this.loadCharts();
  },
  watch: {
    id() {
      this.dataProductCharts = null // Reset to null when id changes to indicate data is not yet loaded
    },
    async dataProductCharts() {
      // Load data when dataProductDetail changes from null to an object
      if (this.dataProductCharts === null) {
        await this.loadCharts();
      }
    }
  },
  mounted() {

  },
  fetchOnServer: false,
  methods: {
    createCharts() {
      console.log('TESTSS');
      this.dataProductCharts.forEach(chart => {
        const ctx = document.getElementById('chart_'+chart.displayName);
        if(ctx !== null)
          this.charts.push(new Chart(ctx, {
            type: 'doughnut',
            data: {
              labels: chart.xAxisValues,
              datasets: [{
                label: chart.displayName,
                data: chart.yAxisValues,
                borderColor: '#1976d2',
                borderWidth: 1
              }]
            },
            options: {
              responsive: true,
            }
          }));
      });
    },
    async fetchDataProductCharts(id) {
      const rawDataProductCharts = await getDataProductCharts(
        this.$axios, id);
      return Object.values(rawDataProductCharts);
    },
    async loadCharts() {
      this.dataProductCharts = await this.fetchDataProductCharts(
        this.id
      )
      setTimeout(() => {
        this.createCharts();
      }, 0);
    },
  },
}
</script>

<style lang="css">
  .v-input__prepend-outer{
      margin-right: 0;
      margin-top: 15px;
  }
  .dataProductDetail {
    width: 50%;
    transform: translate(50%, 0);
  }
  @media screen and (max-width: 600px) {
      .dataProductDetail {
          width: 100%;
          transform: unset;
      }
  }
</style>
