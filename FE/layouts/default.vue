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
      <v-toolbar-title>{{ title }}</v-toolbar-title>
      <v-spacer />
      <v-card-actions v-if="$auth.loggedIn">
        <v-btn text to="/account">{{ getLoggedInUserName() }}</v-btn>
      </v-card-actions>
      <v-card-actions v-else>
        <v-btn text to="/login">Login</v-btn>
        <v-btn text to="/register" class="ml-5">Register</v-btn>
      </v-card-actions>
    </v-app-bar>
    <v-main>
      <Nuxt />
    </v-main>
    <v-footer app>
      <v-btn text to="/impressum">Impressum</v-btn>
      <v-btn text to="/datenschutzerklaerung">Datenschutz</v-btn>
    </v-footer>
  </v-app>
</template>

<script>
  export default
  {
    name: 'DefaultLayout',
    data()
    {
      return{
        clipped: false,
        drawer: false,
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
      getLoggedInUserName()
      {
        if(this.$auth.loggedIn && this.$auth.user !== null)
        {
          return this.$auth.user.userName;
        }
        return '';
      }
    }
  }
</script>