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
        v-else-if="filteredDataProductsOverview.length > 0"
        cols="12"
        md="4"
      >
        <v-card
          v-for="dataProductOverview in filteredDataProductsOverview"
          :key="dataProductOverview.shortKey"
          @click="onClickDataProductDetail(dataProductOverview.shortKey)"
        >
          <data-product-overview-card
            :data-product-overview="dataProductOverview"
          />
        </v-card>
      </v-col>
      <v-col v-else>
        <p>No data products found.</p>
      </v-col>
    </v-row>
    <v-overlay v-if="openedDetails !== ''">
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
      dataProductsOverview: [
        {
          shortKey: String,
          title: String,
          shortDescription: String,
          lastUpdated: Date,
          category: String,
          accessRight: String,
          image: Uint8Array,
        },
      ],
      openedDetails: '',
      isLoading: false,
    }
  },
  async fetch() {
    this.isLoading = true
    this.dataProductsOverview = await this.fetchDataProductsOverview()
    this.filters = this.getDataProductCategories()
    this.isLoading = false
  },
  computed: {
    filteredDataProductsOverview() {
      if (this.filter === '' || this.filter === 'All') {
        return this.dataProductsOverview
      } else {
        return this.dataProductsOverview.filter(
          (dataProduct) =>
            dataProduct.title
              .toLowerCase()
              .includes(this.search.toLowerCase()) &&
            dataProduct.category === this.filter
        )
      }
    },
  },
  methods: {
    async fetchDataProductsOverview() {
      const rawDataProductsOverview = await dataProductsOverviewProvider(
        this.$axios
      )
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
      return dataProductsOverview
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

<style>
.circle {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 70vh;
}
</style>
