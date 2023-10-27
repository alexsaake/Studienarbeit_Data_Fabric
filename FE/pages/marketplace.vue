<template>
  <v-card>
    <v-card v-if="isLoading" class="my-progress">
      <v-progress-circular :size="120" indeterminate color="white"/>
    </v-card>
    <v-container v-else-if="filteredDataProductsOverview.length > 0">
      <v-row>
        <v-col cols="12" md="4">
          <v-text-field v-model="search" label="Suche"></v-text-field>
        </v-col>
        <v-col cols="12" md="4">
          <v-select v-model="filter" :items="filters" label="Kategorie"></v-select>
        </v-col>
        <v-col cols="12" md="4">
          <v-select v-model="sortOrder" :items="sortOrders" label="Sortierung"></v-select>
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
              :average-rating="dataProductOverview.averageRating"
              :user-name="dataProductOverview.userName"
            />
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    <v-card v-else>
      <p>No data products found.</p>
    </v-card>
    <v-overlay v-if="shortKey !== ''" class="my-overlay">
      <data-product-detail-wrapper-card v-click-outside="onCloseDataProduct" :short-key="shortKey" @on-close-data-product="onCloseDataProduct" @on-data-product-deleted="onDataProductDeleted" />
    </v-overlay>
    <overlay-button @custom-click="$auth.loggedIn?$router.push('/newDataProduct'):$router.push('/login?page=newDataProduct');"></overlay-button>
  </v-card>
</template>

<script>
  import {getDataProductImage, getDataProducts, getDataProductAvgRatings} from "~/middleware/dataProductService";
  import DataProductOverviewCard from "~/components/DataProductOverviewCard.vue";
  import DataProductDetailWrapperCard from "~/components/DataProductDetailWrapperCard.vue";
  import OverlayButton from "~/components/OverlayButton.vue";

  export default {
    name: 'Marketplace',
    components: {OverlayButton, DataProductDetailWrapperCard, DataProductOverviewCard},
    beforeRouteLeave (to, from, next) {
      if(!this.shortKey) {
        next()
      } else {
        this.shortKey = '';
        next(false)
      }
    },
    data() {
      return {
        search: '',
        filter: '',
        filters: [''],
        sortOrder: 'Bewertung (abst.)',
        sortOrders: ['Bewertung (abst.)', 'Bewertung (aufst.)', 'Neueste (abst.)', 'Neueste (aufst.)'],
        dataProductsOverview: [],
        isLoading: true, // Initialize the loading state to true
        shortKey: ''
      }
    },
    computed: {
      filteredDataProductsOverview() {
        const search = this.search.toLowerCase()
        const filter = this.filter
        let sortFunction;
        if(this.sortOrder === 'Bewertung (abst.)'){
          sortFunction = function(dataProductA, dataProductB) {return dataProductB.averageRating - dataProductA.averageRating;}
        } else if(this.sortOrder === 'Bewertung (aufst.)'){
          sortFunction = function(dataProductA, dataProductB) {return dataProductA.averageRating - dataProductB.averageRating;}
        } else if(this.sortOrder === 'Neueste (abst.)'){
          sortFunction = function(dataProductA, dataProductB) {return dataProductB.lastUpdated - dataProductA.lastUpdated;}
        } else if(this.sortOrder === 'Neueste (aufst.)') {
          sortFunction = function(dataProductA, dataProductB) {return dataProductA.lastUpdated - dataProductB.lastUpdated;}
        }

        if (filter === '') {
          return this.dataProductsOverview.filter((dataProduct) =>
            dataProduct.title.toLowerCase().includes(search)
          ).sort(sortFunction)
        } else {
          return this.dataProductsOverview.filter(
            (dataProduct) =>
              dataProduct.title.toLowerCase().includes(search) &&
              dataProduct.category === filter
          ).sort(sortFunction)
        }
      },
    },

    async created() {
      await this.fetchData() // Call the fetchData method on component creation
      if(this.$route.query !== undefined && this.$route.query.shortkey !== undefined) {
        const shortKey = this.$route.query.shortkey;
        this.onShowDataProduct(shortKey);
      }
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
              lastUpdated: new Date(dataProduct.lastUpdated),
              category: dataProduct.category,
              accessRight: dataProduct.accessRight,
              image: await getDataProductImage(this.$axios, dataProduct.shortKey),
              averageRating: await getDataProductAvgRatings(this.$axios, dataProduct.shortKey),
              userName: dataProduct.userName
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
        if(document.activeElement.tagName === 'BODY' && sessionStorage.getItem("datePickerOpen") !== "true")
        {
          this.shortKey = '';
        }
      },
      async onDataProductDeleted()
      {
        this.onCloseDataProduct();
        await this.fetchData();
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
    padding: 16px;
  }
  .my-overlay >>> .v-overlay__content
  {
    width: 50%;
    height: 100%;
    overflow-y: scroll;
  }
  @media screen and (max-width: 600px) {
    .my-overlay >>> .v-overlay__content {
      width: 100%;
    }
    .my-overlay
    {
      padding: 0;
    }
  }
  /*@media screen and (max-width: 900px) {*/
  /*    .my-overlay >>> .v-overlay__content {*/
  /*        width: 70%;*/
  /*    }*/
  /*}*/
</style>