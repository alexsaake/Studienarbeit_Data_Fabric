<template>
  <v-container>
    <v-row>
      <v-col cols="12" md="6">
        <v-text-field v-model="search" label="Search"></v-text-field>
      </v-col>
      <v-col cols="12" md="6">
        <v-select v-model="filter" :items="filters" label="Filter"></v-select>
      </v-col>
    </v-row>
    <v-row>
      <v-col v-if="isLoading">
        <div class="circle">
          <v-progress-circular
            :size="120"
            indeterminate
            color="white"
          ></v-progress-circular>
        </div>
      </v-col>
        <v-col
                v-for="dataProductOverview in filteredDataProductsOverview"
                v-else-if="filteredDataProductsOverview.length > 0"
                :key="dataProductOverview.shortKey"
                cols="12"
                md="4"
        >
        <v-card
          style="height: 100%"
          @click="onSelectDataProduct(dataProductOverview.shortKey)"
        >
          <data-product-overview-card
            style="height: 100%"
            :image="dataProductOverview.image"
            :title="dataProductOverview.title"
            :short-description="dataProductOverview.shortDescription"
            :last-updated="dataProductOverview.lastUpdated"
            :access-right="dataProductOverview.accessRight"
          />
        </v-card>
      </v-col>
      <v-col v-else>
        <p>No data products found.</p>
      </v-col>
    </v-row>
    <v-overlay v-if="openedDetails" class="overlay">
      <data-product-detail-overlay :short-key="shortKey" @on-click-outside="onClickOutsideOverlay()" />
    </v-overlay>
  </v-container>
</template>

<script>
import {getDataProductImage, getDataProducts} from "~/middleware/dataProductService";
import DataProductOverviewCard from "~/components/DataProductOverviewCard.vue";
import DataProductDetailOverlay from "~/components/DataProductDetailOverlay.vue";

export default {
  name: 'Marketplace',
  components: {DataProductDetailOverlay, DataProductOverviewCard},
  data() {
    return {
      search: '',
      filter: '',
      filters: ['All'],
      dataProductsOverview: [],
      openedDetails: false,
      isLoading: true, // Initialize the loading state to true
      shortKey: ''
    }
  },
  computed: {
    filteredDataProductsOverview() {
      const search = this.search.toLowerCase()
      const filter = this.filter

      if (filter === '' || filter === 'All') {
        return this.dataProductsOverview.filter((dataProduct) =>
          dataProduct.title.toLowerCase().includes(search)
        )
      } else {
        return this.dataProductsOverview.filter(
          (dataProduct) =>
            dataProduct.title.toLowerCase().includes(search) &&
            dataProduct.category === filter
        )
      }
    },
  },

  async created() {
    await this.fetchData() // Call the fetchData method on component creation
  },

  methods: {
    async fetchData() {
      const rawDataProductsOverview = await getDataProducts(
        this.$axios
      )

      if (Array.isArray(rawDataProductsOverview))
      {
        const dataProductsOverview = [];
        for (const dataProduct of rawDataProductsOverview) {
          dataProductsOverview.push({
            shortKey: dataProduct.shortKey,
            title: dataProduct.title,
            shortDescription: dataProduct.shortDescription,
            lastUpdated: new Date(dataProduct.lastUpdated).toLocaleDateString('ge-GE'),
            category: dataProduct.category,
            accessRight: dataProduct.accessRight,
            image: await getDataProductImage(this.$axios, dataProduct.shortKey),
          });
        }
        this.dataProductsOverview = dataProductsOverview;
        this.filters = this.getDataProductCategories();
      }

      this.isLoading = false // Set loading state to false after fetching data
    },

    getDataProductCategories() {
      const categories = this.dataProductsOverview.map(
        (dataProduct) => dataProduct.category
      )
      return this.filters.concat(
        Array.from(new Set(categories.map((category) => category)))
      )
    },
    onSelectDataProduct(shortKey)
    {
      this.openedDetails = true;
      this.shortKey = shortKey;
    },
    onClickOutsideOverlay()
    {
      this.openedDetails = false;
      this.shortKey = '';
    }
  },
}
</script>

<style>
  .overlay {
    width: 50%;
    height: 100%;
    transform: translate(50%, 0);
    overflow: scroll;
  }
</style>