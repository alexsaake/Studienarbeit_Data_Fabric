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

<script>
export default {
  name: 'InspirePage',
  data() {
    return {
      search: '',
      filter: '',
      filters: ['All', 'Category 1', 'Category 2'],
      items: [
        { id: 1, name: 'Item 1', description: 'Description for item 1', category: 'Category 1' },
        { id: 2, name: 'Item 2', description: 'Description for item 2', category: 'Category 2' },
        { id: 3, name: 'Item 3', description: 'Description for item 3', category: 'Category 1' },
        { id: 4, name: 'Item 4', description: 'Description for item 4', category: 'Category 2' },
      ],
      cart: []
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
  methods: {
    addToCart(item) {
      this.cart.push(item)
    }
  }
}
</script>
