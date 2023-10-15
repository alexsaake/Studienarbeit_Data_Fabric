<template>
  <v-card>
    <v-card v-if="!dataProductCategories || !dataProductAccessRights">
      <v-progress-circular
        :size="120"
        indeterminate
        color="white"
      ></v-progress-circular>
    </v-card>
    <v-container v-else style="margin-top: 50px">
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-text-field
            ref="title"
            v-model="form.title"
            label="Titel"
            :rules="[rules.required, rules.counter]"
            counter
            maxlength="50"
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-text-field
            ref="shortDescription"
            v-model="form.shortDescription"
            label="Kurzbeschreibung"
            :rules="[rules.required, rules.counter]"
            counter
            maxlength="50">
          </v-text-field>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-text-field
            ref="description"
            v-model="form.description"
            label="Beschreibung"
            :rules="[rules.required, rules.counter]"
            counter
            maxlength="50">
          </v-text-field>
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
          <v-select
            ref="category"
            v-model="form.category"
            label="Kategorie"
            :rules="[rules.required]"
            :items="dataProductCategories.categories"
            item-text="value"
            item-value="key"
            ></v-select>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-select
            ref="accessRight"
            v-model="form.accessRight"
            label="Zugriff"
            :rules="[rules.required]"
            :items="dataProductAccessRights.accessRights"
            item-text="value"
            item-value="key"
          ></v-select>
        </v-col>
      </v-row>
    </v-container>
  </v-card>
</template>

<script>
import { getDataProductCategories, getDataProductAccessRights  } from "~/middleware/dataProductService";

export default {
  props: ['clickedNext', 'currentStep'],
  data() {
    return {
      dataProductCategories: null,
      dataProductAccessRights: null,
      form: {
        title: '',
        shortDescription: '',
        description: '',
        source: '',
        sourceLink: '',
        lastUpdate: '',
        category: '',
        accessRight: ''
      },
      rules: {
        required: value => !!value || 'Notwendig.',
        counter: value => value.length <= 50 || 'Max. 50 Zeichen',
      }
    }
  },
  async fetch() {
    this.dataProductCategories = await this.fetchDataProductCategories();
    this.dataProductAccessRights = await this.fetchDataProductAccessRights();
    this.setValidation();
  },
  watch: {
    async dataProductCategories() {
      if (this.dataProductCategories === null) {
        this.dataProductCategories = await this.fetchDataProductCategories();
      }
    },
    async dataProductAccessRights() {
      if (this.dataProductAccessRights === null) {
        this.dataProductAccessRights = await this.fetchDataProductAccessRights();
      }
    },
    form: {
      handler: function (val) {
        this.setValidation();
        this.$emit('data', {
          title: this.form.title,
          description: this.form.description,
          shortDescription: this.form.shortDescription,
          source: this.form.source,
          sourceLink: this.form.sourceLink,
          category: this.form.category,
          accessRight: this.form.accessRight
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
    this.setValidation();
  },
  activated() {
    this.setValidation();
  },
  fetchOnServer: false,
  methods:{
    async fetchDataProductCategories() {
      const rawData = await getDataProductCategories(
        this.$axios,
      );
      return {
        categories: rawData,
      };
    },
    async fetchDataProductAccessRights() {
      const rawData = await getDataProductAccessRights(
        this.$axios,
      );
      return {
        accessRights: rawData,
      };
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
</style>