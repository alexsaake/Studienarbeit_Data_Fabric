import colors from 'vuetify/es5/util/colors'

const dev = process.env.NODE_ENV !== 'production';
export default {
  // Global page headers: https://go.nuxtjs.dev/config-head
  server: {
    host: '0.0.0.0' // default: localhost
  },
  head: {
    titleTemplate: '%s - DataFabricFE',
    title: 'DataFabricFE',
    htmlAttrs: {
      lang: 'en',
    },
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: '' },
      { name: 'format-detection', content: 'telephone=no' },
      { name: 'theme-color', content: '#272727' }
    ],
    link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
  },

  // Global CSS: https://go.nuxtjs.dev/config-css
  css: [],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: true,

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [
    // https://go.nuxtjs.dev/eslint
    '@nuxtjs/eslint-module',
    // https://go.nuxtjs.dev/vuetify
    '@nuxtjs/vuetify',
  ],

  // Modules: https://go.nuxtjs.dev/config-modules
  modules: [
    // https://go.nuxtjs.dev/axios
    '@nuxtjs/axios',
    '@nuxtjs/auth-next',
    '@nuxtjs/proxy'
  ],

  // Axios module configuration: https://go.nuxtjs.dev/config-axios
  axios: {
    // Workaround to avoid enforcing hard-coded localhost:3000: https://github.com/nuxt-community/axios-module/issues/308
    baseURL: '/',
    proxy: true
  },
  auth: {
    strategies: {
      local: {
        scheme: 'refresh',
        token: {
          property: 'accessToken'
        },
        refreshToken: {
          property: 'refreshToken',
          data: 'refreshToken'
        },
        user: {
          property: 'user'
        },
        endpoints: {
          login: { url: '/api/Gateway/Auth/Login', method: 'post' },
          refresh: { url: '/api/Gateway/Auth/Refresh', method: 'post' },
          user: { url: '/api/Gateway/User', method: 'get' },
          logout: false
        }
      }
    },
    redirect: {
      login: '/login',
      logout: '/',
      callback: '/marketplace',
      home: '/marketplace'
    }
  },

  proxy: {
    '/api': {
      // target: 'http://localhost:8443',
      // target: 'http://127.0.0.1:8443',
      target: 'http://'+(dev ? 'localhost' : '192.168.178.11')+':8443',
      // target: 'http://www.immofabric.de:8443',
      changeOrigin: true,
      secure: false,
      ws: true,
      configure: (proxy, _options) => {
        proxy.on('error', (err, _req, _res) => {
          console.log('proxy error', err);
        });
        proxy.on('proxyReq', (proxyReq, req, _res) => {
          console.log('Sending Request to the Target:', req.method, req.url);
        });
        proxy.on('proxyRes', (proxyRes, req, _res) => {
          console.log('Received Response from the Target:', proxyRes.statusCode, req.url);
        });
      },
    }
  },

  // Vuetify module configuration: https://go.nuxtjs.dev/config-vuetify
  vuetify: {
    css: ['~/assets/variables.scss'],
    theme: {
      dark: true,
      themes: {
        dark: {
          primary: colors.blue.darken2,
          accent: colors.grey.darken3,
          secondary: colors.amber.darken3,
          info: colors.teal.lighten1,
          warning: colors.amber.base,
          error: colors.deepOrange.accent4,
          success: colors.green.accent3,
        },
      },
    },
  },

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {
    transpile: [/^vue2-google-maps($|\/)/],
    html:{
      minify:{
        collapseWhitespace: false
      }
    }
  },
}
