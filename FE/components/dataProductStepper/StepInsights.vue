<template>
  <v-card>
    <v-card v-if="!insightTypes || !insightFilterTypes">
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
            ref="generateInsights"
            v-model="form.generateInsights"
            :rules="[rules.required]"
            label="Insights für Datenprodukt generieren"
            :items="['Nein', 'Ja']"
            @change="resetInsightData();resetInsightFilterData()"
          ></v-select>
        </v-col>
      </v-row>
      <v-row  v-if="form.generateInsights==='Ja'" justify="center" >
        <v-col class="col" cols="12" md="9">
          <v-card>
            <v-tabs
              v-model="tabs"
              bg-color="primary"
            >
              <v-tab value="one">Insights Daten</v-tab>
              <v-tab
                value="two"
                :disabled="form.insights.length < 2"
              >Insights Filter</v-tab>
            </v-tabs>
            <v-tabs-items :value="tabs">
              <v-tab-item>
                <v-container
                  v-for="insight in form.insights"
                  :key="insight.id">
                  <v-row justify="center">
                    <v-col class="col" cols="3">
                      <v-select
                        :ref="'insightTypes'+insight.id"
                        v-model="insight.insightType"
                        label="Insight Art"
                        :rules="form.generateInsights==='Ja'?[rules.required]:[]"
                        :items="insightTypes.types"
                        item-text="value"
                        item-value="key"
                        @change="updateInsightRows"
                      ></v-select>
                    </v-col>
                    <v-col class="col" cols="3">
                      <v-select
                        :ref="'dataProductColumn'+insight.id"
                        v-model="insight.dataProductColumn"
                        label="Spalte deines Datenprodukts"
                        :rules="form.generateInsights==='Ja'?[rules.required, rules.validNumericColumn]:[]"
                        :items="getNumericDataColumns()"
                        :disabled="insight.insightType===''"
                      ></v-select>
                    </v-col>
                    <v-col class="col" cols="2">
                      <v-text-field
                        :ref="'unit'+insight.id"
                        v-model="insight.unit"
                        label="Einheit"
                        counter
                        maxlength="10"
                        :disabled="insight.insightType===''"
                      ></v-text-field>
                    </v-col>
                    <v-col class="col" cols="4">
                      <v-text-field
                        :ref="'displayName'+insight.id"
                        v-model="insight.displayName"
                        label="Anzeigename des Insights"
                        counter
                        maxlength="50"
                        :rules="form.generateInsights==='Ja'?[rules.required,rules.counter]:[]"
                        :disabled="insight.insightType===''"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                </v-container>
              </v-tab-item>
              <v-tab-item>
                <v-container
                  v-for="insightFilter in form.insightFilters"
                  :key="insightFilter.id">
                  <v-row justify="center">
                    <v-col class="col" cols="4">
                      <v-select
                        :ref="'insightTypes'+insightFilter.id"
                        v-model="insightFilter.filterType"
                        label="Filter Art"
                        :rules="form.generateInsights==='Ja'?[rules.required]:[]"
                        :items="insightFilterTypes.types"
                        item-text="value"
                        item-value="key"
                        :disabled="form.insights.length < 2"
                        @change="updateInsightFilterRows"
                      ></v-select>
                    </v-col>
                    <v-col class="col" cols="4">
                      <v-select
                        :ref="'dataProductColumn'+insightFilter.id"
                        v-model="insightFilter.dataProductColumn"
                        label="Spalte deines Datenprodukts"
                        :rules="form.generateInsights==='Ja'?[rules.required, value => rules.validColumn(value, insightFilter.filterType)]:[]"
                        :items="getDataColumns(insightFilter.filterType)"
                        :disabled="insightFilter.filterType===''"
                      ></v-select>
                    </v-col>
                    <v-col class="col" cols="4">
                      <v-text-field
                        :ref="'displayName'+insightFilter.id"
                        v-model="insightFilter.displayName"
                        label="Anzeigename des Filters"
                        counter
                        maxlength="50"
                        :rules="form.generateInsights==='Ja'?[rules.required, rules.counter]:[]"
                        :disabled="insightFilter.filterType===''"
                      ></v-text-field>
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
import { getInsightFilterTypes, getInsightTypes } from "~/middleware/dataProductService";

export default {
  props: ['clickedNext', 'currentStep','dataProduct'],
  data() {
    return {
      tabs: null,
      insightTypes: null,
      insightFilterTypes: null,
      form: {
        generateInsights: 'Nein',
        insights:[
          {
            id: 0,
            insightType: '',
            dataProductColumn: '',
            unit: '',
            displayName: '',
          }
        ],
        insightFilters:[
          {
            id: 0,
            filterType: '',
            dataProductColumn: '',
            displayName: '',
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
    this.insightTypes = await this.fetchInsightTypes();
    this.insightFilterTypes = await this.fetchInsightFilterTypes();
    this.setValidation();
  },
  watch: {
    async insightTypes() {
      if (this.insightTypes === null) {
        this.insightTypes = await this.fetchInsightTypes();
      }
    },
    async insightFilterTypes() {
      if (this.insightFilterTypes === null) {
        this.insightFilterTypes = await this.fetchInsightFilterTypes();
      }
    },
    form: {
      handler: function (val) {
        this.setValidation();
        this.$emit('data', {insights: this.form.insights.slice(0,-1), filter: this.form.insightFilters.slice(0,-1)});
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
    this.setValidation();
  },
  async activated() {
    this.setValidation();
    this.insightFilterTypes = await this.fetchInsightFilterTypes();
  },
  methods:{
    async fetchInsightTypes() {
      const rawData = await getInsightTypes(
        this.$axios,
      );
      return {
        types: this.appendAllInput(rawData),
      };
    },
    async fetchInsightFilterTypes() {
      const rawData = await getInsightFilterTypes(
        this.$axios,
      );
      return {
        types: this.appendAllInput(this.checkForMapsLink(rawData)),
      };
    },
    checkForMapsLink(data){
      if(this.dataProduct.mapsData !== null && this.dataProduct.mapsData.linkToMaps !== 'Ja')
        for(let i=0;i<data.length;i++){
          if(data[i].key === "2")
            data.splice(i,1);
        }
      return data;
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
      this.form.insights.forEach(insight => {
        if(insight.insightType !== '' && (insight.displayName === '' || insight.dataProductColumn === ''))
          valid = false;
      });
      this.form.insightFilters.forEach(insightFilter => {
        if(insightFilter.filterType !== '' && (insightFilter.displayName === '' || insightFilter.dataProductColumn === ''))
          valid = false;
      });
      if(this.form.insights.length <= 1 && this.form.generateInsights === 'Ja')
        valid = false;
      return valid;
    },
    getDataColumns(filterType){
      switch (filterType){
        case "1":
        case "3":
          if(this.dataProduct !==null && this.dataProduct.data  !==null  && this.dataProduct.data.length > 0)
            return Object.keys(this.dataProduct.data[0]);
          break;
        case "2":
          return ['postalCode'];
      }

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
    resetInsightData(){
      if(this.form.generateInsights==='Nein')
        this.form.insights = [{id: 0,insightType: '', dataProductColumn: '', displayName: ''}];
    },
    resetInsightFilterData(){
      if(this.form.generateInsights==='Nein')
        this.form.insightFilters = [{id: 0,filterType: '', dataProductColumn: '', displayName: ''}];
    },
    updateInsightRows(){
      for(let i=0;i<this.form.insights.length;i++){
        if(i !== this.form.insights.length - 1 && this.form.insights[i].insightType === ''){
          this.form.insights.splice(i,1);
          i--;
        }
      }
      if(this.form.insights[this.form.insights.length-1].insightType !== '')
        this.form.insights.push({
          id: this.form.insights.length,
          insightType: '',
          dataProductColumn: '',
          displayName: ''
        });
    },
    updateInsightFilterRows(){
      for(let i=0;i<this.form.insightFilters.length;i++){
        if(i !== this.form.insightFilters.length - 1 && this.form.insightFilters[i].filterType === ''){
          this.form.insightFilters.splice(i,1);
          i--;
        }
      }
      if(this.form.insightFilters[this.form.insightFilters.length-1].filterType !== '')
        this.form.insightFilters.push({
          id: this.form.insightFilters.length,
          filterType: '',
          dataProductColumn: '',
          displayName: ''
        });
    }
  }
}
</script>
<style>
.row{
    margin-top: 0;
}
.col{
    padding: 0;
}
</style>