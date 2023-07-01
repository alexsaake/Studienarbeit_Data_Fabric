<template>
  <v-container>
      <table v-if="dataProductData.data !== undefined && dataProductData.data.headline !== undefined">
          <tr class="item-table-row">
              <th v-for="dataHeadline in dataProductData.data.headline" :key="dataHeadline.index" class="item-table-column">
                  {{dataHeadline.value}}
              </th>
          </tr>
          <tr v-for="dataRows in dataProductData.data.body" :key="dataRows.index" class="item-table-row">
              <td v-for="dataColumn in dataRows.value" :key="dataColumn.index" class="item-table-column">
                  {{dataColumn.value}}
              </td>
          </tr>
      </table>
  </v-container>
</template>

<script>
import dataProductData from '~/middleware/dataProductDetailDataProvider'
export default {
    props: {

    },
    data() {
        return {
            dataProductData:{
                headline:[{
                    index: Number,
                    value: String,
                }],
                body:[{
                    index: Number,
                    value:[{
                        index: Number,
                        value: String,
                    }]
                }],
            },
        }
    },
    async fetch() {
        this.dataProductData = await this.fetchData();
    },
    created() {

    },
    fetchOnServer: false,
    methods: {
        async fetchData() {
            const rawScraperData = await dataProductData(this.$axios,this.$route.query.shortKey)
            return {
                data: this.jsonMapTable(rawScraperData)
            }
        },
        jsonMapTable(data) {
            if(data.length < 2)
                return [];

            let head = [];
            const rows = [];
            for (let rowIndex = 0; rowIndex < data.length; rowIndex++){
                const features = [];
                const values = [];

                for (const [key, val] of Object.entries(data[rowIndex])) {
                    features.push({
                        index: features.length,
                        value: key,
                    });
                    values.push({
                        index: values.length,
                        value: val,
                    });
                }
                if(rowIndex === 0)
                    head = features;

                rows.push({
                    index: rows.length,
                    value: values,
                });
            }
            return {
                headline: head,
                body: rows,
            };
        }
    }
}
</script>
<style lang="css">
.container{
    overflow-x: scroll;
}
v-container{
    overflow-x: auto;
    flex-wrap: nowrap;
}
.item-table-row{
    font-size: 14px;
    line-height: 1.25;

}
.item-table-column {
    border-bottom: white 1px solid;
    border-right: white 1px solid;
    overflow-x: auto;
    flex-wrap: nowrap;
    padding: 3px;
}
.item-table-column:last-of-type {
    border-right: none;
}
.item-table-row:last-of-type .item-table-column {
    border-bottom: none;
}
</style>