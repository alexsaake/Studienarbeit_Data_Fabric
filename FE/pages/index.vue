<template>
  <div class="text-center">
    <h1>Welcome to Immo Fabric</h1>
    <p>
      Immo Fabric is a Data Marketplace dedicated to providing valuable real
      estate data insights.
    </p>
    <v-btn color="primary" href="/marketplace"> Explore Marketplace </v-btn>

    <v-card-text>{{page}}</v-card-text>
  </div>
</template>

<script>
export default {
  data(){
    return {
      page: ''
    }
  },
  async created() {
    await this.getSecuredEndpoint();
  },
  methods: {
    async getSecuredEndpoint()
    {
      try {
        await this.$axios.get("/api/Gateway/auth/secured")
            .then(result => {
                if (result.status === 200) {
                  this.page = result.data;
                }
              });
      }
      catch {
        this.page = "Access denied."
      }
    }
  }
}
</script>
