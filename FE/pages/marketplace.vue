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
          <v-col v-for="item in filteredItems" :key="item.id" cols="12" md="4">
            <v-card>
              <v-card-title>{{ item.name }}</v-card-title>
              <v-card-text>{{ item.description }}</v-card-text>
              <v-card-actions>
                <v-btn text @click="addToCart(item)">View Product</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
</template>
    <v-row>
      <v-col cols="12" md="6">
        <v-text-field v-model="search" label="Search"></v-text-field>
      </v-col>
      <v-col cols="12" md="6">
        <v-select v-model="filter" :items="filters" label="Filter"></v-select>
      </v-col>
    </v-row>
    <v-row>
      <v-col v-for="item in filteredItems" :key="item.id" cols="12" md="4">
        <v-card>
          <v-img :src="'data:image/jpeg;base64,' + item.image" height="100px" />
          <v-card-title style="word-break: break-word">{{
            item.name
          }}</v-card-title>
          <v-card-subtitle>
            <div>{{ item.description }}</div>
          </v-card-subtitle>
          <v-card-text>
            <div>Zuletzt aktualisiert: {{ item.lastUpdated }}</div>
            <div>Preis: {{ item.accessRights }}</div>
          </v-card-text>

          <v-card-actions>
            <v-btn text @click="addToCart(item)">View Product</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
<script>
export default {
  name: 'InspirePage',
  data() {
    return {
      search: '',
      filter: '',
      filters: ['All', 'Category 1', 'Category 2'],
      items: [],
    }
  },
  computed: {
    filteredItems() {
      if (this.filter === '' || this.filter === 'All') {
        return this.items.filter(item => item.name.toLowerCase().includes(this.search.toLowerCase()))
      } else {
        return this.items.filter(item => item.name.toLowerCase().includes(this.search.toLowerCase()) && item.category === this.filter)
      }
    }
  },
  created() {
    this.items = [
      {
        id: 1,
        name: this.testData[0].title,
        description: this.testData[0].shortDescription,
        image: this.testData[0].image,
        accessRights: this.testData[0].accessRights,
        lastUpdated: this.testData[0].lastUpdated,
        category: 'Category 1',
      },
      {
        id: 2,
        name: 'Item 2',
        description: 'Description for item 2',
        category: 'Category 2',
      },
      {
        id: 3,
        name: 'Item 3',
        description: 'Description for item 3',
        category: 'Category 1',
      },
      {
        id: 4,
        name: 'Item 4',
        description: 'Description for item 4',
        category: 'Category 2',
      },
    ]
  },
  methods: {
    addToCart(item) {
      this.cart.push(item)
    }
  }
}
</script>
