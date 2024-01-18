<template>
  <div class="stepper-box">
    <div class="top">
      <div class="divider-line" :style="{width: `${(100/(getVisibelStepLength()) * (getVisibelStepLength() - 1)) - 10}%`}"></div>
      <div class="steps-wrapper">
        <template v-if="topButtons">
          <div v-if="currentStep.index > 0" class="stepper-button-top previous" @click="backStep()">
            <i class="material-icons">keyboard_arrow_left</i>
          </div>
        </template>
        <template v-for="(step, index) in steps">
          <div v-if="step.visible" :key="index" :class="['step', isStepActive(index, step)]" :style="{width: `${100 / getVisibelStepLength()}%`}">
            <div class="circle">
              <i class="material-icons md-18">
                {{ (step.completed) ? 'done' : step.icon }}
              </i>
            </div>
            <div class="step-title">
              <h4>{{step.title}}</h4>
              <h5 class="step-subtitle">{{step.subtitle}}</h5>
            </div>
          </div>
        </template>
        <div v-if="topButtons" :class="['stepper-button-top next', !canContinue ? 'deactivated' : '']" @click="nextStep()">
          <i class="material-icons">keyboard_arrow_right</i>
        </div>
      </div>
    </div>
    <div class="content">
      <transition :enter-active-class="enterAnimation" :leave-active-class="leaveAnimation" mode="out-in">
        <!--If keep alive-->
        <keep-alive v-if="keepAliveData">
          <component :is="steps[currentStep.index].component" :clicked-next="nextButton[currentStep.name]" :steps="steps" :current-step="currentStep" :data-product="dataProduct" :data-product-preselect="dataProductPreselect" @can-continue="proceed" @change-next="changeNextBtnValue" @data="setData" @maps-visible-set="mapsVisibleSet"></component>
        </keep-alive>
        <!--If not show component and destroy it in each step change-->
        <component  :is="steps[currentStep.index].component" v-else :clicked-next="nextButton[currentStep.name]" :steps="steps"   :current-step="currentStep" :data-product="dataProduct" :data-product-preselect="dataProductPreselect" @can-continue="proceed" @change-next="changeNextBtnValue" @data="setData" @maps-visible-set="mapsVisibleSet"></component>
      </transition>
    </div>
    <div :class="['bottom', (currentStep.index > 0) ? '' : 'only-next']">
      <div v-if="currentStep.index > 0" class="stepper-button previous" @click="backStep()">
        <i class="material-icons">keyboard_arrow_left</i>
        <span>{{ 'Zurück' }}</span>
      </div>
      <div :class="['stepper-button next', !canContinue ? 'deactivated' : '']" @click="nextStep()">
        <span>{{ (finalStep) ? (dataProductPreselect.state ? 'Änderungen speichern' : 'Datenprodukt erstellen') : 'Weiter' }}</span>
        <i class="material-icons">keyboard_arrow_right</i>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    locale: {
      type: String,
      default: "en"
    },
    topButtons: {
      type: Boolean,
      default: false
    },
    steps: {
      type: Array,
      default: function() {
        return [

        ];
      }
    },
    keepAlive: {
      type: Boolean,
      default: true
    },
    reset: {
      type: Boolean,
      default: false
    },
    dataProductPreselect: {
      type: Object,
      default: () => {
        return  {
          state: false,
          metaData: {},
          insights: [],
          filter: [],
          mapsData: {},
          chartData: [],
          }
      }
    },
  },
  data() {
    return {
      currentStep: {},
      previousStep: {},
      nextButton: {},
      canContinue: false,
      finalStep: false,
      keepAliveData: this.keepAlive,
      dataProduct:{
        metaData: {},
        insights: [],
        filter: [],
        mapsData: {},
        chartData: [],
      }
    };
  },
  computed: {
    enterAnimation() {
      if (this.currentStep.index < this.previousStep.index) {
        return "animated quick fadeInLeft";
      } else {
        return "animated quick fadeInRight";
      }
    },
    leaveAnimation() {
      if (this.currentStep.index > this.previousStep.index) {
        return "animated quick fadeOutLeft";
      } else {
        return "animated quick fadeOutRight";
      }
    }
  },
  watch: {
    reset(val) {
      if(!val) {
        return;
      }
      this.keepAliveData = false;
      this.init();
      this.previousStep = {};
      this.$nextTick(() => {
        this.keepAliveData = this.keepAlive;
        this.$emit('reset', true);
      });
    }
  },
  created() {
    this.init();
  },
  methods: {
    setData(payload){
      if(payload.metaData !== undefined){
        this.dataProduct.metaData = payload.metaData;
        this.dataProduct.metaData.username = this.$auth.user.userName;
      }
      if(payload.data !== undefined){
        this.dataProduct.data = payload.data;
        this.dataProduct.metaData.data = payload.data;
      }
      if(payload.insights !== undefined)
        this.dataProduct.insights = payload.insights;
      if(payload.filter !== undefined)
        this.dataProduct.filter = payload.filter;
      if(payload.mapsData !== undefined)
        this.dataProduct.mapsData = payload.mapsData;
      if(payload.chartData !== undefined)
        this.dataProduct.chartData = payload.chartData;

      console.log(this.dataProduct.metaData);

    },
    isStepActive(index, step) {
      if (this.currentStep.index === index) {
        return "activated";
      } else {
        return "deactivated";
      }
    },
    activateStep(index, back = false) {
      if (this.steps[index]) {
        this.previousStep = this.currentStep;
        this.currentStep = {
          name: this.steps[index].name,
          index
        };
        if (this.isLastStep(index)) {
          this.finalStep = true;
        } else {
          this.finalStep = false;
        }
        if (!back) {
          this.$emit("completed-step", this.previousStep);
        }
      }
      this.$emit("active-step", this.currentStep);
    },
    nextStepAction() {
      this.nextButton[this.currentStep.name] = true;
      if (this.canContinue) {
        if (this.finalStep) {
          this.$emit("stepper-finished", this.dataProduct);
        }
        const currentIndex = this.getNextVisibleIndex();
        this.activateStep(currentIndex);
      }
      this.canContinue = false;
      this.$forceUpdate();
    },
    nextStep () {
      if (!this.$listeners || !this.$listeners['before-next-step']) {
        this.nextStepAction()
      }
      this.canContinue = false;
      this.$emit("before-next-step", { currentStep: this.currentStep }, (next = true) => {
        this.canContinue = true;
        if (next) {
          this.nextStepAction()
        }
      });
    },
    backStep() {
      this.$emit("clicking-back");
      const currentIndex = this.getLastVisibleIndex();
      if (currentIndex >= 0) {
        this.activateStep(currentIndex, true);
      }
    },
    proceed(payload) {
      this.canContinue = payload.value;
    },
    changeNextBtnValue(payload) {
      this.nextButton[this.currentStep.name] = payload.nextBtnValue;
      this.$forceUpdate();
    },
    init() {
      // Initiate stepper
      this.activateStep(0);
      this.steps.forEach(step => {
        this.nextButton[step.name] = false;
      });
    },
    mapsVisibleSet(visible){
      this.$emit("maps-visible-set", visible);
      this.activateStep(this.currentStep.index)
    },
    getVisibelStepLength(){
      let count = 0;
      for(let i=0;i<this.steps.length;i++){
        if(this.steps[i].visible === true)
          count++;
      }
      return count;
    },
    getNextVisibleIndex(){
      let index = this.currentStep.index + 1;
      for(let i = 0;i < this.steps.length;i++){
        if(this.steps[i].visible === true && i >= index) {
          index = i;
          break;
        }
      }
      return index;
    },
    getLastVisibleIndex(){
      let index = this.currentStep.index - 1;
      for(let i = this.steps.length - 1;i >= 0;i--){
        if(this.steps[i].visible === true && i <= index) {
          index = i;
          break;
        }
      }
      return index;
    },
    isLastStep(index){
      let lastStepIndex = 0;
      for(let i = 0;i < this.steps.length;i++){
        if(this.steps[i].visible === true) {
          lastStepIndex = i;
        }
      }
      return (lastStepIndex === index);
    }
  }
};
</script>
<style src="../../css/HorizontalStepper.scss" scoped lang="scss">
</style>
<style scoped>
/* fallback */
@font-face {
    font-family: "Material Icons";
    font-style: normal;
    font-weight: 400;
    src: local("Material Icons"), local("MaterialIcons-Regular"),
    url(https://fonts.gstatic.com/s/materialicons/v17/2fcrYFNaTjcS6g4U3t-Y5ZjZjT5FdEJ140U2DJYC3mY.woff2)
    format("woff2");
}
.material-icons {
    font-family: "Material Icons";
    font-weight: normal;
    font-style: normal;
    font-size: 24px;
    line-height: 1;
    letter-spacing: normal;
    text-transform: none;
    display: inline-block;
    white-space: nowrap;
    word-wrap: normal;
    direction: ltr;
    -webkit-font-feature-settings: "liga";
    -webkit-font-smoothing: antialiased;
}
</style>