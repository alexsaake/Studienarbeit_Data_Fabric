<template>
  <v-card class="dataProductInsights">
    <v-card>
      <div id="map" style="height: 350px;"></div>
    </v-card>
  </v-card>
</template>

<script>
import Vue from "vue";
import * as VueGoogleMaps from 'vue2-google-maps';

Vue.use(VueGoogleMaps, {
  load: {
    key: "AIzaSyBsdnjk_X8Rb_kgJg26R7JTsJYWiRaoce0",
    libraries: "places"
  }
});
let map1;
let markers = [];
export default {
  props: {
    mapsData: {
      type: Array,
      required: false,
      default: null,
    }
  },
  mounted() {
    this.initMap();
  },
  computed: {
    google: VueGoogleMaps.gmapApi
  },
  watch:{
    mapsData(){

      this.removeMarkers();
      this.addMarkers();
    }
  },
  methods: {
    initMap(){
      this.$gmapApiPromiseLazy().then(() => {
        map1 = new this.google.maps.Map(document.getElementById('map'), {
          center: this.calculateCenter(),
          zoom: 12,
          scrollwheel: false,
          draggable: false,
          zoomControl: false,
          disableDoubleClickZoom: true,
          keyboardShortcuts: false
        });
        this.addMarkers();
      });
    },
    addMarkers(){
      if(this.google == null)
        return;
      for (let i = 0; i < this.mapsData.length; i++){
        console.log(this.mapsData[i]);
        if(this.mapsData[i].placeId == null)
          continue;
        const markerLocation = { lat: this.mapsData[i].locationLat, lng: this.mapsData[i].locationLng };
        markers.push(new this.google.maps.Marker({
          position: markerLocation,
          map: map1,
          title: "Hallo\nTest"
        }));
      }
      map1.setCenter(this.calculateCenter());
      this.fitMapToBounds();
    },
    removeMarkers(){
      markers.forEach(marker=>{
        marker.setMap(null);
        marker = null;
      });
      markers = [];
    },
    calculateCenter() {
      if (markers.length === 0) {
        return { lat: 50, lng: 10 }; // Standard-Mittelpunkt, wenn keine Marker vorhanden sind
      }
      let latMin = 0;
      let latMax = 0;
      let lngMin = 0;
      let lngMax = 0;
      markers.forEach(marker=>{
        if(marker.getPosition() === undefined)
          return;
        const lat = marker.getPosition().lat();
        const lng = marker.getPosition().lng();
        if(lat > latMax)
          latMax = lat;
        if(lat < latMin || latMin === 0)
          latMin = lat;
        if(lng > lngMax)
          lngMax = lng;
        if(lng < lngMin || lngMin === 0)
          lngMin = lng;

      });
      const centerLat = (latMin + latMax) / 2;
      const centerLng = (lngMin + lngMax) / 2;
      return { lat: centerLat, lng: centerLng };
    },
    fitMapToBounds() {
      if(markers.length <= 1)
        return map1.setZoom(12);
      const bounds = new this.google.maps.LatLngBounds();
      markers.forEach(marker => {
        if(marker.getPosition() === undefined)
          return;
        bounds.extend(new this.google.maps.LatLng(marker.getPosition().lat(), marker.getPosition().lng()));
      });
      console.log(bounds);
      return map1.fitBounds(bounds);
    }
  }

}
</script>
