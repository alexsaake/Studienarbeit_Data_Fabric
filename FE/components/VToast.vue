<template>
  <v-snackbar
    v-model="showSnackbar"
    :color="color"
    :timeout="timer"
    bottom
  >
    <v-icon left>{{icon}}</v-icon>{{message}}
  </v-snackbar>
</template>

<script>
export default {
  name: "VToast",
  data() {
    return{
      showSnackbar: false,
      message: '',
      color: 'success',
      icon: 'mdi-check',
      timer: 5000
    }
  },
  methods:{
    show(data) {
      this.message = data.message || 'missing "message".'
      this.color = data.color || 'primary'
      this.timer = data.timer || 5000
      this.icon = data.icon || 'mdi-check'
      this.showSnackbar = true
    }
  }
}
</script>