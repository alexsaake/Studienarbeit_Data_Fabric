<template>
  <v-card>
    <v-card-title>Datenprodukt erstellen</v-card-title>
    <horizontal-stepper :steps="steps" @completed-step="completeStep" :top-buttons="true"
                      @active-step="isStepActive" @stepper-finished="alert"></horizontal-stepper>
  </v-card>
</template>

<script>
import HorizontalStepper from "~/components/dataProductStepper/HorizontalStepper.vue";
import StepMetaData from "~/components/dataProductStepper/StepMetaData.vue";
import StepProductData from "~/components/dataProductStepper/StepProductData.vue";
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
      ],
      activeStep: 0
    }
  },
  computed: {},
  methods: {
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
    alert(payload) {
      alert('end')
    }
  }

};
</script>

<style scoped>

</style>