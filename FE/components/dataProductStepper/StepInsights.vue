<template>
  <v-container style="margin-top: 50px">
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-select
          ref="generateInsights"
          v-model="form.generateInsights"
          :rules="[rules.required]"
          label="Insights für Datenprodukt generieren"
          :items="['Nein', 'Ja']"
        ></v-select>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: ['clickedNext', 'currentStep'],
  data() {
    return {
      form: {
        generateInsights: 'Nein',
      },
      rules: {
        required: value => !!value || 'Notwendig.',
        counter: value => value.length <= 50 || 'Max. 50 Zeichen',
        email: value => {
          const pattern =
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
          return pattern.test(value) || 'Ungültige Email'
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
    }
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