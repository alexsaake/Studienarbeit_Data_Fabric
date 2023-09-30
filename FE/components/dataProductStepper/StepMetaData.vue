<template>
  <v-container style="margin-top: 50px">
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-text-field
          ref="title"
          v-model="form.title"
          label="Titel"
          :rules="[rules.required, rules.counter]"
          counter
          maxlength="20"
        ></v-text-field>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-text-field v-model="form.shortDescription" label="Kurzbeschreibung"></v-text-field>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-text-field v-model="form.description" label="Beschreibung"></v-text-field>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-text-field v-model="form.source" label="Quelle"></v-text-field>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-text-field v-model="form.sourceLink" label="Quellen-Link"></v-text-field>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-text-field v-model="form.lastUpdate" label="Zuletzt akualisiert"></v-text-field>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col class="col" cols="12" md="6">
        <v-text-field v-model="form.category" label="Kategorie"></v-text-field>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import {validationMixin} from 'vuelidate'
export default {
  props: ['clickedNext', 'currentStep'],
  mixins: [validationMixin],
  data() {
    return {
      form: {
        title: '',
        shortDescription: '',
        description: '',
        source: '',
        sourceLink: '',
        lastUpdate: '',
        category: '',
      },
      rules: {
        required: value => !!value || 'Required.',
        counter: value => value.length <= 20 || 'Max 20 characters',
        email: value => {
          const pattern =
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
          return pattern.test(value) || 'Invalid e-mail.'
        }
      }
    }
  },
  watch: {
    form: {
      handler: function (val) {
        if(this.checkFormValidation()) {
          this.$emit('can-continue', {value: true});
        } else {
          this.$emit('can-continue', {value: false});
          setTimeout(()=> {
            this.$emit('change-next', {nextBtnValue: false});
          }, 3000)
        }
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
    this.$nextTick(() => {
      if (this.checkFormValidation()) {
        this.$emit('can-continue', { value: true });
      } else {
        this.$emit('can-continue', { value: false });
      }
    });
  },
  activated() {
    if (this.checkFormValidation()) {
      this.$emit('can-continue', { value: true });
    } else {
      this.$emit('can-continue', { value: false });
    }
  },
  methods:{
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