<template>
  <v-card>
    <v-card v-if="!chartTypes">
      <v-progress-circular
        :size="120"
        indeterminate
        color="white"
      ></v-progress-circular>
    </v-card>
    <v-container v-else style="margin-top: 50px">
      <v-row justify="center">
        <v-col class="col" cols="12" md="9">
          <v-select
            ref="createCharts"
            v-model="form.createCharts"
            :rules="[rules.required]"
            label="Diagramme für Datenprodukt erstellen"
            :items="['Nein', 'Ja']"
            @change="resetChartData()"
          ></v-select>
        </v-col>
      </v-row>
      <v-row  v-if="form.createCharts==='Ja'" justify="center" >
        <v-col class="col" cols="12" md="9">
          <v-card>
            <v-tabs
              v-model="tabs"
              bg-color="primary"
            >
              <v-tab
                v-for="chart in form.charts" :key="chart.id"
                value="one">Diagramm {{chart.id + 1}}</v-tab>
            </v-tabs>
            <v-tabs-items :value="tabs">
              <v-tab-item
                v-for="chart in form.charts" :key="chart.id">
                <v-container>
                  <v-row>
                    <v-col class="col">
                      <v-select
                        :ref="'chartTypes'+chart.id"
                        v-model="chart.chartType"
                        label="Diagramm Art"
                        :rules="form.createCharts==='Ja'?[rules.required]:[]"
                        :items="chartTypes.types"
                        item-text="value"
                        item-value="key"
                        @change="updateChartRows"
                      ></v-select>
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        :ref="'displayName'+chart.id"
                        v-model="chart.displayName"
                        label="Anzeigename des Diagramms"
                        counter
                        maxlength="50"
                        :rules="form.createCharts==='Ja'?[rules.required,rules.counter]:[]"
                        :disabled="chart.chartType===''"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="col">
                      <div style="display: flex">
                        <v-select
                          :ref="'yValueType'+chart.id"
                          v-model="chart.yValueType"
                          :items="[
                          {
                            value: 'Werte einzeln',
                            key: 1,
                          },
                          {
                            value: 'Durchschnitt gleicher Werte',
                            key: 2,
                          },
                          {
                            value: 'Anzahl gleicher Werte',
                            key: 3,
                          }
                        ]"
                          item-text="value"
                          item-value="key"
                          label="Typ"
                          :disabled="chart.chartType===''"
                        ></v-select>
                        <v-icon
                          v-if="chart.yValueType === 1 || chart.yValueType === 2"
                          color="info"
                          @click="$root.VToast.show({message: 'Mit diesem Typ sind nur nummerische Daten innerhalb der y-Achsen Spalte erlaubt!', color: 'info', icon: 'mdi-information'});"
                        >mdi-information</v-icon>
                      </div>
                    </v-col>
                    <v-col class="col">
                      <v-checkbox
                        :ref="'fillChart'+chart.id"
                        v-model="chart.fillChart"
                        :false-value="0"
                        :true-value="1"
                        label="Hintergrund füllen"
                        :disabled="chart.chartType===''"
                        @change="test(chart.fillChart)"
                      ></v-checkbox>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="col">
                      <v-text-field
                        :ref="'xAxisName'+chart.id"
                        v-model="chart.xAxisName"
                        label="Name der x-Achse"
                        :rules="form.createCharts==='Ja'?[rules.required]:[]"
                        counter
                        maxlength="50"
                        :disabled="chart.chartType===''"
                      ></v-text-field>
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        :ref="'xAxisUnit'+chart.id"
                        v-model="chart.xAxisUnit"
                        label="Einheit der x-Achse"
                        counter
                        maxlength="50"
                        :disabled="chart.chartType===''"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="col">
                      <v-select
                        :ref="'xAxisDataproductColumn'+chart.id"
                        v-model="chart.xAxisDataproductColumn"
                        label="X-Achsen Spalte"
                        :rules="form.createCharts==='Ja'?[rules.required, rules.validColumn]:[]"
                        :items="getDataColumns()"
                        :disabled="chart.chartType===''"
                      ></v-select>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="col">
                      <v-text-field
                        :ref="'yAxisName'+chart.id"
                        v-model="chart.yAxisName"
                        label="Name der y-Achse"
                        :rules="form.createCharts==='Ja'?[rules.required]:[]"
                        counter
                        maxlength="50"
                        :disabled="chart.chartType===''"
                      ></v-text-field>
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        :ref="'yAxisUnit'+chart.id"
                        v-model="chart.yAxisUnit"
                        label="Einheit der y-Achse"
                        counter
                        maxlength="50"
                        :disabled="chart.chartType===''"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="col">
                     <v-card-title>
                       Datensets
                     </v-card-title>
                    </v-col>
                  </v-row>
                  <v-row  v-for="dataset in chart.datasets" :key="dataset.id">
                    <v-col class="col">
                      <v-text-field
                        :ref="'datasetDisplayName'+dataset.displayName"
                        v-model="dataset.displayName"
                        label="Name des Datensets"
                        :rules="form.createCharts==='Ja'?[rules.required]:[]"
                        counter
                        maxlength="50"
                        :disabled="chart.chartType===''"
                        @change="updateChartRows"
                      ></v-text-field>
                    </v-col>
                    <v-col class="col">
                      <v-select
                        :ref="'xAxisDataproductColumn'+dataset.yAxisDataproductColumn"
                        v-model="dataset.yAxisDataproductColumn"
                        label="Y-Achsen Spalte"
                        :rules="form.createCharts==='Ja'?[rules.required, (chart.yValueType===3?rules.validColumn:rules.validNumericColumn)]:[]"
                        :items="(chart.yValueType===3?getDataColumns():getNumericDataColumns())"
                        :disabled="chart.chartType==='' || dataset.displayName===''"
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-container>
              </v-tab-item>
            </v-tabs-items>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-card>
</template>

<script>
import {getChartTypes } from "~/middleware/dataProductService";

export default {
  props: ['steps','clickedNext', 'currentStep','dataProduct','dataProductPreselect'],
  data() {
    return {
      tabs: null,
      chartTypes: null,
      form: {
        createCharts: 'Nein',
        charts:[
          {
            id: 0,
            chartType: '',
            xAxisName: '',
            yAxisName: '',
            xAxisUnit: '',
            yAxisUnit: '',
            xAxisDataproductColumn: '',
            displayName: '',
            yValueType: 1,
            fillChart: 0,
            datasets:[
              {
                id: 0,
                displayName: '',
                yAxisDataproductColumn: '',
              }
            ]
          }
        ],
      },
      rules: {
        required: value => !!value || 'Notwendig.',
        counter: value => value.length <= 50 || 'Max. 50 Zeichen',
        validColumn: (value, type) => {
          if(this.getDataColumns(type) === undefined)
            return 'Keine gültige Spalte';
          return this.getDataColumns(type).includes(value) || 'Keine gültige Spalte';
        },
        validNumericColumn: value => {
          if(this.getNumericDataColumns() === undefined)
            return 'Keine gültige Spalte';
          return this.getNumericDataColumns().includes(value) || 'Keine gültige Spalte';
        }
      }
    }
  },
  async fetch() {
    this.chartTypes = await this.fetchChartTypes();
    this.setValidation();
  },
  watch: {
    async chartTypes() {
      if (this.chartTypes === null) {
        this.chartTypes = await this.fetchChartTypes();
      }
    },
    form: {
      handler: function (val) {
        this.setValidation();
        console.log(this.dataProductPreselect.chartData);
        this.$emit('data', {
          chartData: this.form.charts.slice(0,-1)
        });
      },
      deep: true
    },
    clickedNext(val) {
      if(val === true && this.form.$touch !== undefined) {
        this.form.$touch();
      }
    }
  },
  mounted() {
    this.preset();
    this.setValidation();
  },
  activated() {
    this.setValidation();
  },
  methods:{
    test(test){
      test = 0;
    },
    preset(){
      console.log('#PreSet',this.dataProductPreselect);
      if(this.dataProductPreselect.chartData.length > 0){
        this.form.createCharts = 'Ja';
        this.dataProductPreselect.chartData.forEach(element => {
          const index = this.form.charts.push({
            id: this.form.charts.length,
            chartType: element.type+"",
            xAxisName: element.xAxisName,
            yAxisName: element.yAxisName,
            xAxisUnit: element.xAxisUnit,
            yAxisUnit: element.yAxisUnit,
            xAxisDataproductColumn: element.xAxisDataproductColumn,
            displayName: element.displayName,
            yValueType: element.yValueType,
            fillChart: element.fillChart,
            datasets: [],
          }) -1;
          element.datasets.forEach(element2 => {
            this.form.charts[index].datasets.push({
              id: this.form.charts[index].datasets.length,
              displayName: element2.displayName,
              yAxisDataproductColumn: element2.yAxisDataproductColumn,
            });

          })
        })
        this.updateChartRows();
      }
    },
    setIDs(){
      for(let i = 0; i < this.form.charts.length; i++) {
        this.form.charts[i].id = i;
      }
    },
    async fetchChartTypes() {
      const rawData = await getChartTypes(
        this.$axios,
      );
      return {
        types: this.appendAllInput(rawData),
      };
    },
    appendAllInput(data){
      data.unshift('');
      return data;
    },
    setValidation(){
      this.$nextTick(() => {
        if(this.checkFormValidation()) {
          this.$emit('can-continue', {value: true});
        } else {
          this.$emit('can-continue', {value: false});
          setTimeout(()=> {
            this.$emit('change-next', {nextBtnValue: false});
          }, 3000)
        }
      });
    },
    checkFormValidation() {
      let valid = true;
      Object.keys(this.form).forEach(f => {
        if (this.$refs !== undefined && this.$refs[f] !== undefined) {
          this.$refs[f].validate();
          if (this.$refs[f].hasError)
            valid = false;
        }
      })
      this.form.charts.forEach(chart => {
        if(chart.chartType === '')
          return;
        if(chart.displayName === '' || chart.xAxisName === '' || !this.getDataColumns().includes(chart.xAxisDataproductColumn) || chart.yAxisName === '')
          valid = false;
        chart.datasets.forEach(dataset => {
          if(dataset.displayName === '')
            return;
          if(!this.getDataColumns().includes(dataset.yAxisDataproductColumn))
            valid = false;
        });
        if(chart.datasets.length <= 1 && this.form.createCharts === 'Ja')
          valid = false;
      });
      if(this.form.charts.length <= 1 && this.form.createCharts === 'Ja')
        valid = false;
      return valid;
    },
    getDataColumns(){
      if(this.dataProduct !==null && this.dataProduct.data  !==null  && this.dataProduct.data.length > 0)
        return Object.keys(this.dataProduct.data[0]);

      return [];
    },
    getNumericDataColumns(){
      if(this.dataProduct ===null || this.dataProduct.data ===null || this.dataProduct.data.length <= 0)
        return [];
      const keys = [];
      for (const [key, value] of Object.entries(this.dataProduct.data[0])) {
        if(typeof value === 'number')
          keys.push(key);
      }
      return keys;
    },
    resetChartData(){
      if(this.form.createCharts==='Nein')
        this.form.charts = [{
          id: 0,
          chartType: '',
          xAxisName: '',
          yAxisName: '',
          xAxisUnit: '',
          yAxisUnit: '',
          xAxisDataproductColumn: '',
          displayName: '',
          yValueType: 1,
          fillChart: 0,
          datasets:[
            {
              id: 0,
              displayName: '',
              yAxisDataproductColumn: '',
            }
          ]
        }];
    },
    updateChartRows(){
      for(let i=0;i<this.form.charts.length;i++){
        if(i !== this.form.charts.length - 1 && this.form.charts[i].chartType === ''){
          this.form.charts.splice(i,1);
          i--;
        }
      }
      if(this.form.charts[this.form.charts.length-1].chartType !== '')
        this.form.charts.push({
          id: this.form.charts.length,
          chartType: '',
          xAxisName: '',
          yAxisName: '',
          xAxisUnit: '',
          yAxisUnit: '',
          xAxisDataproductColumn: '',
          displayName: '',
          yValueType: 1,
          fillChart: 0,
          datasets:[
            {
              id: 0,
              displayName: '',
              yAxisDataproductColumn: '',
            }
          ]
        });
      for(let j=0;j<this.form.charts.length;j++) {
        for (let i = 0; i < this.form.charts[j].datasets.length; i++) {
          if (i !== this.form.charts[j].datasets.length - 1 && this.form.charts[j].datasets[i].displayName === '') {
            this.form.charts[j].datasets.splice(i, 1);
            i--;
          }
        }
        if (this.form.charts[j].datasets[this.form.charts[j].datasets.length - 1].displayName !== '')
          this.form.charts[j].datasets.push({
            id: this.form.charts[j].datasets.length,
            displayName: '',
            yAxisDataproductColumn: '',

          });
      }
      this.setIDs();
    },
  }
}
</script>
<style>
.row{
    margin-top: 0;
}
.col{
    padding: 0 5px!important;
}
</style>