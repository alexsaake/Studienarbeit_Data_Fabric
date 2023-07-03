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
import {getSecuredEndpoint} from "~/middleware/authService";

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
        await getSecuredEndpoint(this.$axios)
          .then(result => {
            this.page = result.data;
          });
      }
      catch {
        this.page = "Access denied."
      }
    }
  }
}
</script>
