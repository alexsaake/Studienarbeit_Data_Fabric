<template>
  <v-container style="margin-top: 50px">
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-select
          ref="linkToMaps"
          v-model="form.linkToMaps"
          :rules="[rules.required]"
          label="Datenprodukt mit Google Maps verbinden"
          :items="['Nein', 'Ja']"
          @input="form.linkToMaps==='Nein'?form.mapsAddressCity='':'';form.linkToMaps==='Nein'?form.mapsAddressStreet='':''"
        ></v-select>
      </v-col>
    </v-row>
    <v-row v-if="form.linkToMaps==='Ja'" justify="center">
      <v-col class="col" cols="12" md="6">
        <v-card>
          <h4>Welche Spalten deines Datenprodukts sollen dafür verwendet werden?</h4>
          <v-select
            ref="mapsAddressCity"
            v-model="form.mapsAddressCity"
            label="Adresse (Stadt)"
            :rules="form.linkToMaps==='Ja'?[rules.required, rules.validColumn]:[]"
            :items="getDataColumns()"
          ></v-select>
          <v-select
            ref="mapsAddressStreet"
            v-model="form.mapsAddressStreet"
            label="Adresse (Straße)"
            :rules="form.linkToMaps==='Ja'?[rules.required, rules.validColumn]:[]"
            :items="getDataColumns()"
          ></v-select>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: ['clickedNext', 'currentStep','dataProduct'],
  data() {
    return {
      form: {
        linkToMaps:'Nein',
        mapsAddressCity:'',
        mapsAddressStreet:'',
      },
      rules: {
        required: value => !!value || 'Notwendig',
        counter: value => value.length <= 50 || 'Max. 50 Zeichen',
        validColumn: value => {
          if(this.getDataColumns() === undefined)
            return 'Keine gültige Spalte';
          return this.getDataColumns().includes(value) || 'Keine gültige Spalte';
        }
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
    },
  },
  mounted() {
    this.setValidation();
  },
  activated() {
    this.setValidation();
  },
  methods:{
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
    },
    getDataColumns(){
      if(this.dataProduct !==null && this.dataProduct.data !==null && this.dataProduct.data.length > 0)
      return Object.keys(this.dataProduct.data[0]);
    }
  }
}
</script>
<style>
.row{
    margin-top: 0;
}
.col{
    padding: 0 10px;
}
h4{
    margin-top: 15px;
    margin-bottom: 15px;
}
</style>