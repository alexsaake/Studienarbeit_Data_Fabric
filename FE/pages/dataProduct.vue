<template>
  <v-card>
    <v-card-title>{{(dataProductPreselect.state ? 'Datenprodukt bearbeiten' : 'Datenprodukt erstellen')}}</v-card-title>
    <horizontal-stepper
      :steps="steps"
      :top-buttons="true"
      :data-product-preselect="dataProductPreselect"
      @completed-step="completeStep"
      @active-step="isStepActive"
      @stepper-finished="uploadData"
    ></horizontal-stepper>
    <v-snackbar
      v-model="snackbar"
      :timeout="timeout"
    >
      {{snackbarText}}

      <template v-slot:actions>
        <v-btn
          color="primary"
          variant="text"
          @click="snackbar = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-card>
</template>

<script>
import HorizontalStepper from "~/components/dataProductStepper/HorizontalStepper.vue";
import StepMetaData from "~/components/dataProductStepper/StepMetaData.vue";
import StepProductData from "~/components/dataProductStepper/StepProductData.vue";
import StepMapsData from "~/components/dataProductStepper/StepMapsData.vue";
import StepInsights from "~/components/dataProductStepper/StepInsights.vue";
import {
  getDataProductDataAll,
  insertDataProduct,
  updateDataProduct,
} from "~/middleware/dataProductService";
export default {
  name: "newDataProduct",
  components: {
    HorizontalStepper,
  },
  data() {
    return {
      id: -1,
      steps: [
        {
          name: 'metaData',
          title: 'Grunddaten',
          subtitle: 'Gebe hier die Grunddaten deines Produktes ein',
          component: StepMetaData,
          completed: false
        },
        {
          name: 'dataProduct',
          title: 'Datenprodukt',
          subtitle: 'Lade dein Datenprodukt hoch',
          component: StepProductData,
          completed: false
        },
        {
          name: 'mapsData',
          title: 'Google Maps Verbindung',
          subtitle: 'Verbinde dein Datenprodukt mit Google Maps und erhalte somit genaue Standorte zu deinen Daten',
          component: StepMapsData,
          completed: false
        },
        {
          name: 'insights',
          title: 'Generiere Insights',
          subtitle: 'Erstelle deine eigenen Insights zu deinem Datenprodukt, um dieses noch interessanter zu machen',
          component: StepInsights,
          completed: false
        },
      ],
      activeStep: 0,
      dataProductPreselect:{
        state: false,
        metaData: {},
        insights: [],
        filter: [],
        mapsData: {},
      }
    }
  },
  computed: {},
  created() {
    if(this.$route.query !== undefined && this.$route.query.id !== undefined) {
      this.id = this.$route.query.id;
      this.preselectData(this.id);
    }
  },
  mounted() {
    if (!this.$auth.loggedIn)
      this.$router.push('/login?page=dataProduct');
  },
  methods: {
    async uploadDataProduct(data) {
      return await insertDataProduct(
        this.$axios, data
      );
    },
    async editDataProduct(data, id) {
      return await updateDataProduct(
        this.$axios, data, id
      );
    },
    async loadDataProduct(id) {
      return await getDataProductDataAll(
        this.$axios, id
      );
    },
    async preselectData(id){
      const data = await this.loadDataProduct(id);
      if(data !== undefined){
        this.dataProductPreselect.state = true;
        if(data.metaData !== undefined) {
          if(this.$auth.loggedIn && data.metaData.username !== this.$auth.user.userName){
            window.location.href = '/dataProduct';
          }
          this.dataProductPreselect.metaData = data.metaData;
        }
        if(data.insights !== undefined) {
          this.dataProductPreselect.insights = data.insights;
        }
        if(data.insightFilters !== undefined) {
          this.dataProductPreselect.insightFilters = data.insightFilters;
        }
        if(data.mapsData !== undefined) {
          this.dataProductPreselect.mapsData = data.mapsData;
        }
      }
    },
    completeStep(payload) {
      this.steps.forEach((step) => {
        if (step.name === payload.name) {
          step.completed = true;
        }
      })
    },
    isStepActive(payload) {
      this.steps.forEach((step) => {
        if (step.name === payload.name) {
          if (step.completed === true) {
            step.completed = false;
          }
        }
      })
    },
    async uploadData(payload) {
      if(this.dataProductPreselect.state)
        await this.editData(payload);
      else
        await this.createData(payload);
    },
    async createData(payload) {
      const id = await this.uploadDataProduct(payload);
      if(id !== -1){
        this.$root.VToast.show({message: 'Datenprodukt wurde erfolgreich erstellt!'});
        await this.$router.push('/marketplace?id='+ id);
      }else{
        this.$root.VToast.show({message: 'Datenprodukt konnte nicht angelegt werden!', color: 'error', icon: 'mdi-close'});
      }
    },
    async editData(payload) {
      const ret = await this.editDataProduct(payload, this.id);
      if(ret){
        this.$root.VToast.show({message: 'Datenprodukt wurde erfolgreich gespeichert!'});
        await this.$router.push('/marketplace?id='+this.id);
      }else{
        this.$root.VToast.show({message: 'Datenprodukt konnte nicht gespeichert werden!', color: 'error', icon: 'mdi-close'});
      }
    }
  }
}
</script>

<style scoped>

</style>