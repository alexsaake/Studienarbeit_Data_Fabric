<template>
  <v-card>
    <v-card v-if="isLoading" class="my-progress">
      <v-progress-circular :size="120" indeterminate color="white"/>
    </v-card>
    <v-container v-else-if="filteredDataProductsOverview.length > 0">
      <v-row>
        <v-col cols="12" md="6">
          <v-text-field v-model="search" label="Search"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-select v-model="filter" :items="filters" label="Filter"></v-select>
        </v-col>
      </v-row>
      <v-row>
        <v-col
          v-for="dataProductOverview in filteredDataProductsOverview"
          :key="dataProductOverview.shortKey"
          cols="12" md="4"
        >
          <v-card style="height: 100%" @click="onShowDataProduct(dataProductOverview.shortKey)">
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
      </v-row>
    </v-container>
    <v-card v-else>
      <p>No data products found.</p>
    </v-card>
    <v-overlay v-if="shortKey !== ''" class="my-overlay">
      <data-product-detail-wrapper-card v-click-outside="onCloseDataProduct" :short-key="shortKey" />
    </v-overlay>
  </v-card>
</template>

<script>
  import {getDataProductImage, getDataProducts} from "~/middleware/dataProductService";
  import DataProductOverviewCard from "~/components/DataProductOverviewCard.vue";
  import DataProductDetailWrapperCard from "~/components/DataProductDetailWrapperCard.vue";

  export default {
    name: 'Marketplace',
    components: {DataProductDetailWrapperCard, DataProductOverviewCard},
    data() {
      return {
        search: '',
        filter: '',
        filters: ['All'],
        dataProductsOverview: [],
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
      onShowDataProduct(shortKey)
      {
        this.shortKey = shortKey;
      },
      onCloseDataProduct()
      {
        if(document.activeElement.tagName === 'BODY'&&sessionStorage.getItem("datePickerOpen")==="false")
          this.shortKey = '';
      }
    },
  }
</script>

<style scoped>
  .my-progress
  {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .my-overlay
  {
    width: 100%;
    height: 100%;
  }
  .my-overlay >>> .v-overlay__content
  {
    width: 50%;
    height: 100%;
    overflow-y: scroll;
  }
</style>