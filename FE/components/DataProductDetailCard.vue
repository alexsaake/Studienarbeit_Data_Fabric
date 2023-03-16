<template>
  <v-card id="dataProductDetail">
    <v-img :src="'data:text/plain;base64,' + dataProductDetail.image" height="300px" />
    <v-card-title style="word-break: break-word">{{dataProductDetail.title}}</v-card-title>
    <v-card-subtitle>{{ dataProductDetail.shortDescription }}</v-card-subtitle>
    <v-card-text>
      {{ dataProductDetail.description }}<br>
      Quelle: {{ dataProductDetail.source }}<br>
      Quellen-Link: <v-card>{{dataProductDetail.sourceLink}}</v-card>
      Zuletzt aktualisiert: {{ dataProductDetail.lastUpdated }}<br>
      Kategorie: {{ dataProductDetail.category }}<br>
      Zugriff: {{ dataProductDetail.accessRight }}
    </v-card-text>
  </v-card>
</template>

<script>
  import dataProductImageProvider from "~/middleware/dataProductImageProvider";
  import dataProductDetailProvider from "~/middleware/dataProductDetailProvider";

  export default
  {
    props:
    {
      shortKey:
      {
        type: String,
        required: false,
        default: ''
      }
    },
    data()
    {
      return {
        dataProductDetail:
        {
          shortKey: String,
          title: String,
          shortDescription: String,
          description: String,
          source: String,
          sourceLink: String,
          lastUpdated: Date,
          category: String,
          accessRight: String,
          image: Uint8Array
        }
      }
    },
    async fetch()
    {
      this.dataProductDetail = await this.fetchDataProductDetail(this.shortKey)
    },
    watch:
      {
        "shortKey": "$fetch"
      },
    fetchOnServer: false,
    methods:
    {
      async fetchDataProductDetail(shortKey)
      {
        const rawDataProductDetail = await dataProductDetailProvider(this.$axios, shortKey);
        return {
          shortKey: rawDataProductDetail.shortKey,
          title: rawDataProductDetail.title,
          shortDescription: rawDataProductDetail.shortDescription,
          description: rawDataProductDetail.description,
          source: rawDataProductDetail.source,
          sourceLink: rawDataProductDetail.sourceLink,
          lastUpdated: new Date(rawDataProductDetail.lastUpdated).toLocaleDateString("ge-GE"),
          category: rawDataProductDetail.category,
          accessRight: rawDataProductDetail.accessRight,
          image: await dataProductImageProvider(this.$axios, rawDataProductDetail.shortKey)
        };
      }
    }
  }
</script>

<style lang="css">
  #dataProductDetail {
    width: 50%;
    height: 50%;
    alignment: center;
    transform: translate(50%, 0);
  }
</style>