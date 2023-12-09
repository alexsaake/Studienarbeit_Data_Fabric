<template>
  <v-card>
    <v-card v-if="isLoading" class="my-progress">
      <v-progress-circular :size="120" indeterminate color="white"/>
    </v-card>
    <v-container v-else-if="dataProductsOverview.length > 0">
      <v-row>
        <template v-if="$auth.loggedIn">
          <v-col cols="12" md="3">
            <v-text-field v-model="search" label="Suche"></v-text-field>
          </v-col>
          <v-col cols="12" md="3">
            <v-select v-model="filter" :items="filters" label="Kategorie"></v-select>
          </v-col>
          <v-col cols="12" md="3">
            <v-select v-model="sortOrder" :items="sortOrders" label="Sortierung"></v-select>
          </v-col>
          <v-col cols="12" md="3">
            <v-checkbox v-model="showMyDataProducts" label="Meine Datenprodukte anzeigen"></v-checkbox>
          </v-col>
        </template>
        <template v-else>
          <v-col cols="12" md="4">
            <v-text-field v-model="search" label="Suche"></v-text-field>
          </v-col>
          <v-col cols="12" md="4">
            <v-select v-model="filter" :items="filters" label="Kategorie"></v-select>
          </v-col>
          <v-col cols="12" md="4">
            <v-select v-model="sortOrder" :items="sortOrders" label="Sortierung"></v-select>
          </v-col>
        </template>
      </v-row>
      <v-row>
        <v-col
          v-for="dataProductOverview in filteredDataProductsOverview"
          :key="dataProductOverview.id"
          cols="12" md="4"
        >
          <v-lazy :min-height="200" :options="{'threshold':0.5}" transition="fade-transition">
            <v-card style="height: 100%">
              <data-product-overview-card
                style="height: 100%"
                :id="dataProductOverview.id"
                :title="dataProductOverview.title"
                :userName="dataProductOverview.userName"
                :last-updated="dataProductOverview.lastUpdated"
                :average-rating="dataProductOverview.averageRating"
                :access-rights-catalogue="accessRightsCatalogue"
                @on-select-data-product="onShowDataProduct"
              />
            </v-card>
          </v-lazy>
        </v-col>
      </v-row>
    </v-container>
    <v-card v-else>
      <p>No data products found.</p>
    </v-card>
    <v-overlay v-if="selectedDataProduct.id !== -1" class="my-overlay">
      <data-product-detail-wrapper-card
          v-click-outside="onCloseDataProduct"
          :id="selectedDataProduct.id"
          :image-file-name="selectedDataProduct.imageFileName"
          :title="selectedDataProduct.title"
          :short-description="selectedDataProduct.shortDescription"
          :last-updated="selectedDataProduct.lastUpdated"
          :access-right="selectedDataProduct.accessRight"
          :category="selectedDataProduct.category"
          :average-rating="selectedDataProduct.averageRating"
          :user-name="selectedDataProduct.userName"
          @on-close-data-product="onCloseDataProduct"
          @on-data-product-deleted="onDataProductDeleted"
          @on-edit-data-product="onEditDataProduct" />
    </v-overlay>
    <overlay-button @custom-click="$auth.loggedIn?$router.push('/dataProduct'):$router.push('/login?page=dataProduct');"></overlay-button>
  </v-card>
</template>

<script>
import {
  getDataProducts,
  getDataProductAvgRatings,
  getDataProduct,
  getDataProductAccessRights, getDataProductCategories
} from "~/middleware/dataProductService";
  import DataProductOverviewCard from "~/components/DataProductOverviewCard.vue";
  import DataProductDetailWrapperCard from "~/components/DataProductDetailWrapperCard.vue";
  import OverlayButton from "~/components/OverlayButton.vue";

  export default {
    name: 'Marketplace',
    components: {OverlayButton, DataProductDetailWrapperCard, DataProductOverviewCard},
    beforeRouteLeave (to, from, next) {
      if(to.fullPath === '/login') {
        if(this.selectedDataProduct.id !== -1) {
          this.selectedDataProduct.id = -1;
        }
        next();
      } else {
        next();
      }
    },
    data() {
      return {
        search: '',
        filter: '',
        filters: [''],
        categoriesCatalogue: null,
        accessRightsCatalogue: null,
        showMyDataProducts: false,
        sortOrder: 'Bewertung (abst.)',
        sortOrders: ['Bewertung (abst.)', 'Bewertung (aufst.)', 'Neueste (abst.)', 'Neueste (aufst.)'],
        dataProductsOverview: [],
        selectedDataProduct: {
          id: -1,
          image: '',
          title: '',
          shortDescription: '',
          lastUpdated: new Date(),
          accessRight: '',
          category: '',
          averageRating: 0,
          userName: ''
        },
        isLoading: true
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
          if (this.showMyDataProducts) {
            return this.dataProductsOverview.filter((dataProduct) =>
                dataProduct.title.toLowerCase().includes(search) &&
                dataProduct.userName === this.$auth.user.userName
            ).sort(sortFunction)
          } else {
            return this.dataProductsOverview.filter((dataProduct) =>
                dataProduct.title.toLowerCase().includes(search)
            ).sort(sortFunction)
          }
        } else if (this.showMyDataProducts) {
          return this.dataProductsOverview.filter(
              (dataProduct) =>
                  dataProduct.title.toLowerCase().includes(search) &&
                  dataProduct.category === filter &&
                  dataProduct.userName === this.$auth.user.userName
          ).sort(sortFunction)
        } else {
          return this.dataProductsOverview.filter(
              (dataProduct) =>
                  dataProduct.title.toLowerCase().includes(search) &&
                  dataProduct.category === filter
          ).sort(sortFunction)
        }
      }
    },

    async created() {
      await this.fetchData(); // Call the fetchData method on component creation
      if(this.$route.query !== undefined && this.$route.query.id !== undefined) {
        const id = Number.parseInt(this.$route.query.id.toString());
        const dataProduct = await getDataProduct(this.$axios, id);
        const dataProductOverview = this.dataProductsOverview.find(obj => { return obj.id === id });
        if(dataProduct.imageFileName === null) {
          dataProduct.imageFileName = "defaultImage.jpg";
        }

        this.onShowDataProduct(id,
            dataProduct.imageFileName,
            dataProductOverview.title,
            dataProduct.shortDescription,
            dataProductOverview.lastUpdated,
            this.accessRightsCatalogue[dataProduct.accessRightsId],
            dataProductOverview.averageRating,
            dataProductOverview.userName);
      }
    },
    methods: {
      async fetchData() {
        const rawDataProductsOverview = await getDataProducts(this.$axios);
        this.categoriesCatalogue = await getDataProductCategories(this.$axios);
        this.filters = Object.values(this.categoriesCatalogue);
        this.filters.unshift('');
        this.accessRightsCatalogue = await getDataProductAccessRights(this.$axios);

        if (Array.isArray(rawDataProductsOverview))
        {
          const dataProductsOverview = [];
          for (const dataProduct of rawDataProductsOverview) {
            dataProductsOverview.push({
              id: dataProduct.id,
              title: dataProduct.title,
              userName: dataProduct.userName,
              lastUpdated: new Date(dataProduct.lastUpdated),
              category: this.categoriesCatalogue[dataProduct.categoryId],
              averageRating: await getDataProductAvgRatings(this.$axios, dataProduct.id)
            });
          }
          this.dataProductsOverview = dataProductsOverview;
        }

        this.isLoading = false; // Set loading state to false after fetching data
      },

      onShowDataProduct(id, imageFileName, title, shortDescription, lastUpdated, accessRight, averageRating, userName)
      {
        const objs = this.dataProductsOverview.filter(obj => { return obj.id === id });
        if(objs.length > 0){
          const category = objs[0].category;
          this.selectedDataProduct = {
            id,
            imageFileName,
            title,
            shortDescription,
            lastUpdated,
            accessRight,
            category,
            averageRating,
            userName
          };
          this.disableBackgroundScrolling();
        }
      },
      onCloseDataProduct()
      {
        if(document.activeElement.tagName === 'BODY' && sessionStorage.getItem("datePickerOpen") !== "true")
        {
          this.selectedDataProduct.id = -1;
          this.enableBackgroundScrolling();
        }
      },
      async onDataProductDeleted()
      {
        this.onCloseDataProduct();
        await this.fetchData();
      },
      onEditDataProduct()
      {
        this.$router.push('/dataProduct?id=' + this.selectedDataProduct.id);
      },
      disableBackgroundScrolling() {
        document.documentElement.style.overflow = 'hidden';
      },
      enableBackgroundScrolling() {
        document.documentElement.style.overflow = 'unset';
      },
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