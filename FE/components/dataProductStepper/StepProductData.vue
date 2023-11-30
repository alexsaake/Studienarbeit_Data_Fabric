<template>
  <v-container style="margin-top: 50px">

    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-tooltip bottom left>
          <template v-slot:activator='{ on }'>
          <v-file-input
            ref="file"
            v-model="form.file"
            :rules="[rules.required, rules.file]"
            accept="application/JSON, .csv"
            placeholder="Wähle deine Daten aus"
            label="Rohdaten"
            type="file"
            @change="handleFileUpload"
            @mouseenter.native='on.mouseenter'
            @mouseleave.native='on.mouseleave'>
          ></v-file-input>
          </template>
          <span>CSV or JSON like:
              [
                {
                  "id": 1,
                  "jahr": "2021",
                  "frauen": "3 699",
                  "maenner": "4 275",
                  "insgesamt": "4 100"
                },
                {
                  "id": 2,
                  "jahr": "2020",Marina 30 zusammen
                  "frauen": "3 578",
                  "maenner": "4 146",
                  "insgesamt": "3 975"
                }
              ]
          </span>
        </v-tooltip>
      </v-col>
    </v-row>
    <v-row v-if="(jsonData !== null && jsonData.length > 0)" justify="center">
      <v-col class="col" cols="12" md="6">
        <v-card>
          <v-card-text class="v-card-text">Anzahl an Datensätzen: {{form.rowCount}}</v-card-text>
          <v-card-text class="v-card-text">Anzahl an Spalten: {{form.columnCount}}</v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: ['clickedNext', 'currentStep','dataProductPreselect'],
  data() {
    return {
      jsonData: null,
      form: {
        file:'',
        columnCount: null,
        rowCount: null
      },
      rules: {
        required: value => !!value || (this.jsonData !== null && this.jsonData.length > 0) || 'Notwendig',
        counter: value => value.length <= 50 || 'Max. 50 Zeichen',
        file: value => this.checkFileType(value)
      }
    }
  },
  watch: {
    form: {
      handler: function (val) {
        this.setValidation();
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
    this.presetJsonData();
    this.setValidation();
  },
  activated() {
    this.setValidation();
  },
  methods:{
    presetJsonData(){
      if (this.dataProductPreselect.metaData.data === undefined)
        return;
      this.jsonData = this.parseJson(this.parseJson(this.dataProductPreselect.metaData.data));
      this.setCountData(this.jsonData);
      //
      this.$emit('data', {data: this.jsonData});
    },
    async handleFileUpload() {
      if (this.form.file) {
        const fileContent = await this.readFileAsync(this.form.file);
        if (fileContent) {
          const extension = this.form.file.name.split('.').pop();
          if (extension === 'csv') {
            this.jsonData = this.parseCsv(fileContent);
          } else if (extension === 'json') {
            this.jsonData = this.parseJson(fileContent);
          }
        }
        if(!this.validateJSONData()) {
          this.jsonData = null;
          this.form.file = null;
          this.$root.VToast.show({message: 'Datei konnte nicht verarbeitet werden. Bitte überprüfe das Schema deiner Daten!', color: 'error', icon: 'mdi-close'});
          return;
        }
        this.setCountData(this.jsonData);
        //
        this.$emit('data', {data: this.jsonData});
      }else{
        this.jsonData = null;
      }
    },
    readFileAsync(file) {
      return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onload = (e) => resolve(e.target.result);
        reader.readAsText(file);
      });
    },
    validateJSONData(){
      if(this.jsonData == null)
        return false;
      if (!Array.isArray(this.jsonData))
        return false;
      for(const json of this.jsonData){
        if(typeof json !== 'object' || Array.isArray(json) || json === null)
          return false;
      }
      return true;
    },
    parseCsv(csvContent) {
      const lines = csvContent.split('\n');
      const headers = lines[0].split(',');
      const results = [];

      for (let i = 1; i < lines.length; i++) {
        const currentLine = lines[i].split(',');
        const entry = {};

        for (let j = 0; j < headers.length; j++) {
          entry[headers[j]] = currentLine[j];
        }
        results.push(entry);
      }
      if(results.length <= 0)
        return null;

      return results;
    },
    parseJson(jsonContent) {
      try {
        return JSON.parse(jsonContent);
      } catch (error) {
        console.error('Fehler beim Parsen der JSON-Datei:', error);
        return null;
      }
    },
    setCountData(data){
      if(data == null)
        return;
      this.form.rowCount = data.length;
      if(this.form.rowCount > 0)
        this.form.columnCount = Object.keys(data[0]).length;
      else
        this.form.columnCount = 0;
    },
    checkFileType(val){
      if (this.jsonData !== null && this.jsonData.length > 0)
        return true;
      if(val === null)
        return 'Notwendig';
      return val.type === 'application/vnd.ms-excel' || val.type === 'application/json' || 'Ungültiger Dateientyp'
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
      return valid;
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
.v-card-text{
    padding: 5px 15px;
}
</style>