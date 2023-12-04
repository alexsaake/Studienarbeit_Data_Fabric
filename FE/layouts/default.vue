<template>
  <v-app dark>
    <v-navigation-drawer
      v-model="drawer"
      :left="$vuetify.breakpoint.mdAndUp"
      :clipped="clipped"
      fixed
      app
    >
      <v-list>
        <v-list-item
          v-for="(page, i) in pages"
          :key="i"
          :to="page.to"
          router
          exact
          @click="closeDrawerOnLoad"
        >
          <v-list-item-action>
            <v-icon>{{ page.icon }}</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title>{{ page.title }}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-app-bar :clipped-left="clipped" fixed app>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
<!--      <v-btn icon @click.stop="miniVariant = !miniVariant">-->
<!--        <v-icon>mdi-{{ `chevron-${miniVariant ? 'right' : 'left'}` }}</v-icon>-->
<!--      </v-btn>-->
<!--      <v-btn icon @click.stop="clipped = !clipped">-->
<!--        <v-icon>mdi-application</v-icon>-->
<!--      </v-btn>-->
      <v-toolbar-title v-if="!easterEggShow" @click="kaggleEasterEgg">{{ title }}</v-toolbar-title>
      <v-toolbar-title v-if="easterEggShow" ><a href="https://www.kaggle.com/" class="kaggle">KAGGLE</a> Fabric</v-toolbar-title>
      <v-spacer />
      <v-card-actions>
        <div v-show="$auth.loggedIn">
          <v-btn text to="/account">{{ getLoggedInUserName() }}</v-btn>
        </div>
        <div v-show="!$auth.loggedIn">
          <v-btn text to="/login">Login</v-btn>
          <v-btn text to="/register">Register</v-btn>
        </div>
      </v-card-actions>
    </v-app-bar>
    <v-main>
      <Nuxt />
    </v-main>
    <v-footer app>
      <v-btn text to="/impressum">Impressum</v-btn>
      <v-btn text to="/datenschutzerklaerung">Datenschutz</v-btn>
    </v-footer>
    <VToast ref="VToast"/>
  </v-app>
</template>

<script>
import VToast from '~/components/VToast.vue'
  export default
  {
    name: 'DefaultLayout',
    components: { VToast },
    data()
    {
      return{
        easterEggCount: 0,
        easterEggShow: false,
        clipped: false,
        drawer: true,
        pages: [
          {
            icon: 'mdi-apps',
            title: 'Welcome',
            to: '/',
          },
          {
            icon: 'mdi-store',
            title: 'Marketplace',
            to: '/marketplace',
          },
          {
            icon: 'mdi-pencil',
            title: 'Create product',
            to: '/dataProduct',
          }
        ],
        title: 'Data Fabric'
      }
    },
    watch:{
      easterEggCount:{
        handler: function (val) {
          if(this.easterEggCount >= 6)
            this.easterEggCount = 0;
          },
        deep: true
      },

    },
    mounted() {
      this.$root.VToast = this.$refs.VToast;
      this.closeDrawerOnLoad();
    },
    methods: {
      closeDrawerOnLoad() {
        this.$nextTick(() => {
          this.drawer = false; // Set drawer to false after the component is mounted
        });
      },
      getLoggedInUserName()
      {
        if(this.$auth.loggedIn && this.$auth.user !== null)
        {
          return this.$auth.user.userName;
        }
        return '';
      },
      kaggleEasterEgg(){
        if(this.easterEggCount === 0){
          const timer = setTimeout(() => {
            this.easterEggCount=0;
            clearTimeout(timer);
          }, 1000);
        }
        //
        if(this.easterEggCount === 4){
          this.easterEggShow = true;
          const timer2 = setTimeout(() => {
            this.easterEggShow = false;
            clearTimeout(timer2);
          }, 2000);
        }
        this.easterEggCount++;
      }
    }
  }
</script>
<style>
.kaggle{
    text-decoration: none;
    color: #20BEFF !important;
    font-family: zeitung, sans-serif !important;
}
</style>
