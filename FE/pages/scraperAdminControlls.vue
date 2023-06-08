<template>
  <v-container>
    <v-row>
      <v-col cols="12" md="4">
        <v-text-field v-model="city" label="Stadt"></v-text-field>
      </v-col>
      <v-col v-if="scraperData.status !== 'running' && scraperData.status !== 'stopping'" cols="12" md="2" >
          <v-btn @click="startScraper()">Starte Scraper</v-btn>
      </v-col>
        <v-col v-if="scraperData.status === 'running' || scraperData.status === 'stopping'" cols="12" md="2">
            <v-btn  @click="stopScraper()">Stoppe Scraper</v-btn>
        </v-col>
    </v-row>
    <v-row>
        <v-col cols="6" md="2">
            <div>Status:</div>
        </v-col>
        <v-col cols="6" md="2">
            <div>{{scraperData.status}}</div>
        </v-col>
    </v-row>
    <v-row>
        <v-col cols="6" md="2">
            <div>Gescrapte Seiten:</div>
        </v-col>
        <v-col cols="6" md="2">
            <div>{{scraperData.pageCount}}</div>
        </v-col>
    </v-row>
    <v-row>
        <v-col cols="6" md="2">
            <div>Gescrapte Items:</div>
        </v-col>
        <v-col cols="6" md="2">
            <div>{{scraperData.itemCount}}</div>
        </v-col>
    </v-row>
    <v-row class="item-table-row" v-for="items in scraperData.items" :key="items.index">
        <v-col class="item-table-column" cols="6" md="1">
            <div>{{items.index}}</div>
        </v-col>
        <v-col class="item-table-column" cols="6" md="5">
            <div>{{items.title}}</div>
        </v-col>
    </v-row>

  </v-container>
</template>

<script>
import {get as getScraperData} from "~/middleware/scraperDataProvider";
import {patch as setScraperData} from "~/middleware/scraperDataProvider";
import {post as startScraping} from "~/middleware/scraperDataProvider";
export default {
    data() {
        return {
            items:[{
                index: Number,
                title: String
            }],
            city: '',
            scraperData:[{
                status: String,
                itemCount: Number,
                pageCount: Number,
                items:[{
                    index: Number,
                    title: String
                }]
            }]
        }
    },
    async fetch() {
        this.scraperData = await this.fetchScraperData();
        this.intervalFetchData();
    },
    created() {

    },
    fetchOnServer: false,
    methods: {
        async fetchScraperData() {
            const rawScraperData = await getScraperData(this.$axios)
            return {
                status: rawScraperData.status,
                itemCount: rawScraperData.itemCount,
                pageCount: rawScraperData.pageCount,
                items: rawScraperData.items
            }
        },
        async startScraper() {
            return await startScraping(this.$axios, this.city).then(response => {
                if (response.status === 200)
                    alert(response.data);
            })
        },
        async stopScraper() {
            await setScraperData(this.$axios, {status:'stop'});
        },
        intervalFetchData() {
            setInterval(async () => {
                this.scraperData = await this.fetchScraperData();
            }, 5000);
        }
    }
}
</script>
<style lang="css">
.item-table-column {
    border-bottom: white 1px solid;
}
</style>