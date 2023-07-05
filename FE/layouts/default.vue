<template>
  <v-app dark>
    <v-navigation-drawer
      v-model="drawer"
      :mini-variant="miniVariant"
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
      <v-btn icon @click.stop="miniVariant = !miniVariant">
        <v-icon>mdi-{{ `chevron-${miniVariant ? 'right' : 'left'}` }}</v-icon>
      </v-btn>
      <v-btn icon @click.stop="clipped = !clipped">
        <v-icon>mdi-application</v-icon>
      </v-btn>
      <v-btn icon @click.stop="fixed = !fixed">
        <v-icon>mdi-minus</v-icon>
      </v-btn>
      <v-toolbar-title>{{ title }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-card-actions v-if="!$auth.loggedIn">
        <v-btn href="login">Login</v-btn>
        <v-btn href="register">Register</v-btn>
      </v-card-actions>
      <v-card-actions v-else>
        <v-btn @click="onLogout()">Logout</v-btn>
      </v-card-actions>
    </v-app-bar>
    <v-main>
      <v-container>
        <Nuxt />
      </v-container>
    </v-main>
    <v-footer :absolute="!fixed" app>
      <v-btn text href="/impressum">Impressum</v-btn>
      <v-btn text href="/datenschutzerklaerung">Datenschutz</v-btn>
    </v-footer>
  </v-app>
</template>

<script>
  export default {
    name: 'DefaultLayout',
    data() {
      return {
        clipped: false,
        drawer: false,
        fixed: false,
        pages: [
          {
            icon: 'mdi-apps',
            title: 'Welcome',
            to: '/',
          },
          {
            icon: 'mdi-chart-bubble',
            title: 'Marketplace',
            to: '/marketplace',
          }
        ],
        miniVariant: false,
        right: true,
        rightDrawer: false,
        title: 'Data Fabric'
      }
    },
    methods: {
      async onLogout(){
        await this.$auth.logout()
          .then(() => {
            window.location.reload();
          });
      }
    }
  }
</script>
