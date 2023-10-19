<template>
  <v-card>
    <v-card-title>Datenprodukt erstellen</v-card-title>
    <horizontal-stepper :steps="steps" @completed-step="completeStep" :top-buttons="true"
                      @active-step="isStepActive" @stepper-finished="uploadData"></horizontal-stepper>
  </v-card>
</template>

<script>
import HorizontalStepper from "~/components/dataProductStepper/HorizontalStepper.vue";
import StepMetaData from "~/components/dataProductStepper/StepMetaData.vue";
import StepProductData from "~/components/dataProductStepper/StepProductData.vue";
import StepMapsData from "~/components/dataProductStepper/StepMapsData.vue";
import StepInsights from "~/components/dataProductStepper/StepInsights.vue";
import { insertDataProduct, insertInsightFilter, insertInsights } from "~/middleware/dataProductService";
export default {
  name: "newDataProduct",
  components: {
    HorizontalStepper,
  },
  data(){
    return {
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
      activeStep: 0
    }
  },
  computed: {},
  mounted() {
    if(!this.$auth.loggedIn)
      this.$router.push('/login?page=newDataProduct');
  },
  methods: {
    async uploadDataProduct(shortkey, data) {
      return await insertDataProduct(
        this.$axios, shortkey, data
      );
    },
    async uploadInsights(shortkey, data) {
      return await insertInsights(
        this.$axios, shortkey, data
      );
    },
    async uploadInsightFilter(shortkey, data) {
      return await insertInsightFilter(
        this.$axios, shortkey, data
      );
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
          if(step.completed === true) {
            step.completed = false;
          }
        }
      })
    },
    async uploadData(payload) {
      const ret = await this.uploadDataProduct(payload.product.title, payload.product);
      if(ret === true){
        const ret2 = await this.uploadInsights(payload.product.title, payload.insights);
        if(ret2 === true){
          const ret3 = await this.uploadInsightFilter(payload.product.title, payload.filter);
          if(ret3 === true){
            alert('Datenprodukt wurde erfolgreich angelegt!');
            window.location.href = "/marketplace?shortkey=" + payload.product.title;
            return;
          }
        }
      }
      alert('Datenprodukt konnte nicht angelegt werden!');
    }
  }

};
</script>

<style scoped>

</style>