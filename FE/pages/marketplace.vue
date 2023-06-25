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
          @click="onClickDataProductDetail(dataProductOverview.shortKey)"
        >
          <data-product-overview-card
            style="height: 100%"
            :data-product-overview="dataProductOverview"
          />
        </v-card>
      </v-col>
      <v-col v-else>
        <p>No data products found.</p>
      </v-col>
    </v-row>
    <v-overlay v-if="openedDetails !== ''" class="my-overlay">
      <data-product-detail-card
        v-click-outside="onClickOutsideDataProductDetail"
        :short-key="openedDetails"
      />
    </v-overlay>
  </v-container>
</template>

<script>
import dataProductsOverviewProvider from '~/middleware/dataProductsOverviewProvider'
import dataProductImageProvider from '~/middleware/dataProductImageProvider'

export default {
  name: 'Marketplace',
  data() {
    return {
      search: '',
      filter: '',
      filters: ['All'],
      dataProductsOverview: [],
      openedDetails: '',
      isLoading: true, // Initialize the loading state to true
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
      const rawDataProductsOverview = await dataProductsOverviewProvider(
        this.$axios
      )

      if (Array.isArray(rawDataProductsOverview)) {
        const dataProductsOverview = []
        for (const dataProduct of rawDataProductsOverview) {
          dataProductsOverview.push({
            shortKey: dataProduct.shortKey,
            title: dataProduct.title,
            shortDescription: dataProduct.shortDescription,
            lastUpdated: new Date(dataProduct.lastUpdated).toLocaleDateString(
              'ge-GE'
            ),
            category: dataProduct.category,
            accessRight: dataProduct.accessRight,
            image: await dataProductImageProvider(
              this.$axios,
              dataProduct.shortKey
            ),
          })
        }
        this.dataProductsOverview = dataProductsOverview
        this.filters = this.getDataProductCategories()
      } else {
        // markus: removed error because it always shows up if data is not instantly loaded
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
    onClickDataProductDetail(shortKey) {
      this.openedDetails = shortKey
    },
    onClickOutsideDataProductDetail() {
      this.openedDetails = ''
    },
  },
}
</script>
<style scoped>
.my-overlay >>> .v-overlay__content {
    width: 100%;
}
</style>
<style>
.circle {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 70vh;
}
</style>
