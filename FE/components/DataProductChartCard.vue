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
            v-for="chart in dataProductCharts" :key="chart.displayName"
            :eager="true">
            <canvas :id="'chart_'+chart.displayName"></canvas>
          </v-tab-item>
        </v-tabs-items>
      </div>

    </v-card>
  </v-card>
</template>
<script>
import Chart from 'chart.js';
import { getDataProductCharts } from "~/middleware/dataProductService";


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
      CHART_COLORS: [
        'rgba(25,118,210,0.75)',
        'rgba(255,99,132,0.75)',
        'rgba(255,159,64,0.75)',
        'rgba(255,205,86,0.75)',
        'rgba(75,192,192,0.75)',
        'rgba(54,162,235,0.75)',
        'rgba(153,102,255,0.75)',
        'rgba(201,203,207,0.75)',
        'rgba(171,68,89,0.75)',
        'rgba(197,122,47,0.75)',
        'rgba(182,146,61,0.75)',
        'rgba(55,140,140,0.75)',
        'rgba(44,109,154,0.75)',
        'rgba(102,69,166,0.75)',
        'rgba(135,136,140,0.75)'
      ]
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
      this.dataProductCharts.forEach(chart => {
        const ctx = document.getElementById('chart_'+chart.displayName);
        if(ctx !== null)
          this.charts.push(new Chart(ctx, {
            type: this.getType(chart),
            data: {
              labels: chart.xAxisValues,
              datasets: this.getDatasets(chart),
            },
            options: {
              responsive: true,
              scales: {
                xAxes: [{
                  display: this.showAxisLabel(chart),
                  scaleLabel: {
                    display: this.showAxisLabel(chart),
                    labelString: chart.xAxisName
                  }
                }],
                yAxes: [{
                  display: this.showAxisLabel(chart),
                  scaleLabel: {
                    display: this.showAxisLabel(chart),
                    labelString: chart.yAxisName
                  },
                  ticks: {
                    beginAtZero: true
                  }
                }]
              },
            }
          }));
      });
    },
    getDatasets(chart){
      const ret = [];
      for(let i = 0;i < chart.datasets.length;i++){
        ret.push({
            label: chart.datasets[i].displayName,
            data: chart.datasets[i].datasetValues,
            borderWidth: this.getBorderWidth(chart),
            backgroundColor: this.getBackgroundColor(chart, i),
            borderColor: this.getColor(chart, i),
          })
      }
      return ret;
    },
    getBorderWidth(chart){
      if(chart.type === 4)
        return 0;
      return 1;
    },
    getColor(chart, datasetIndex) {
      if(chart.type === 3 || chart.type === 5){
        return this.CHART_COLORS
      }
      return this.CHART_COLORS[datasetIndex];
    },
    getBackgroundColor(chart, datasetIndex) {
      if(!chart.fillChart)
        return undefined;
      if(chart.type === 3 || chart.type === 5){
        return this.CHART_COLORS
      }
      return this.CHART_COLORS[datasetIndex];
    },
    getType(chart){
      switch (chart.type){
        case 1:
          return 'line';
        case 2:
          return 'bar';
        case 3:
          return 'pie';
        case 4:
          return 'line';
        case 5:
          return 'doughnut';
      }
    },
    showAxisLabel(chart){
      if(chart.type === 3 || chart.type === 5){
        return false;
      }
      return true;
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
