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
            :rules="[rules.required]"
            counter
            maxlength="128"
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-textarea
            ref="shortDescription"
            v-model="form.shortDescription"
            label="Kurzbeschreibung"
            rows="1"
            :rules="[rules.required]"
            counter
            auto-grow
            maxlength="1024">
          </v-textarea>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-textarea
            ref="description"
            v-model="form.description"
            label="Beschreibung"
            rows="1"
            :rules="[rules.required]"
            counter
            auto-grow
            maxlength="4096">
          </v-textarea>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-text-field
              v-model="form.source"
              counter
              label="Quelle"
              maxlength="128"
          ></v-text-field>

        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-text-field
            v-model="form.sourceLink"
            label="Quellen-Link"
            counter
            maxlength="1024"
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-select
            ref="category"
            v-model="form.category"
            label="Kategorie"
            :rules="[rules.required]"
            :items="dataProductCategories"
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
            :items="dataProductAccessRights"
            item-text="value"
            item-value="key"
          ></v-select>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col class="col" cols="12" md="6">
          <v-file-input
              label="Bild hochladen"
              @change="onFileSelected"
              accept="image/*"
              outlined
          ></v-file-input>
          <v-btn
              :disabled="!selectedFile"
              color="primary"
              @click="uploadImage"
          >Hochladen</v-btn>
        </v-col>
      </v-row>

    </v-container>
  </v-card>
</template>

<script>
import { getDataProductCategories, getDataProductAccessRights, uploadDataProductImage  } from "~/middleware/dataProductService";

export default {
  props: ['clickedNext', 'currentStep','dataProductPreselect'],
  data() {
    return {
      dataProductCategories: null,
      dataProductAccessRights: null,
      selectedFile: null,
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
      }
    }
  },
  async fetch() {
    this.dataProductCategories = await this.fetchDataProductCategories();
    this.dataProductAccessRights = await this.fetchDataProductAccessRights();
    this.setValidation();
  },
  watch: {
    dataProductPreselect:{
      handler: function (val) {
        console.log(this.dataProductPreselect.metaData);
        this.form.title = this.dataProductPreselect.metaData.title;
        this.form.shortDescription = this.dataProductPreselect.metaData.shortDescription;
        this.form.description = this.dataProductPreselect.metaData.description;
        this.form.source = this.dataProductPreselect.metaData.source;
        this.form.sourceLink = this.dataProductPreselect.metaData.sourceLink;
        this.form.lastUpdate = this.dataProductPreselect.metaData.lastUpdate;
        this.form.category = this.dataProductPreselect.metaData.categoryId + "";
        this.form.accessRight = this.dataProductPreselect.metaData.accessRightId + "";
      },
      deep: true
    },
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
          metaData:{
            title: this.form.title,
            description: this.form.description,
            shortDescription: this.form.shortDescription,
            source: this.form.source,
            sourceLink: this.form.sourceLink,
            category: this.form.category,
            accessRight: this.form.accessRight
          }
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
    onFileSelected(file) {
      if (file) {
        this.selectedFile = file;
      } else {
        this.selectedFile = null;
      }
    },
    async uploadImage() {
      if (!this.selectedFile) return;

      // Assuming 'id' is available in the component's data
      const id = this.dataProductPreselect.metaData.id;
      try {
        const response = await uploadDataProductImage(this.$axios, id, this.selectedFile);
        console.log('Upload response:', response);
        // Handle the successful upload here (e.g., show a success message or update the UI)
      } catch (error) {
        console.error('Upload error:', error.toString());
        // Handle the error here (e.g., show an error message)
      }
    },
    async fetchDataProductCategories() {
      const rawData = await getDataProductCategories(
        this.$axios,
      );
      return Object.entries(rawData).map(([key, value]) => ({ key, value }));
    },
    async fetchDataProductAccessRights() {
      const rawData = await getDataProductAccessRights(
        this.$axios,
      );
      return Object.entries(rawData).map(([key, value]) => ({ key, value }));
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