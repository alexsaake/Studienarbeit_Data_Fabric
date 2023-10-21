package com.mse.datafabric.utils.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleMapsAddressDTO {
    public int dataId;

    public String city;
    public String street;
    public String placeId;
    public String postalCode;
    public double locationLat;
    public double locationLng;


    @JsonCreator
    public GoogleMapsAddressDTO(@JsonProperty("city")String city, @JsonProperty("street")String street){
        this.city = city;
        this.street = street;
        this.dataId = -1;
    }
    public GoogleMapsAddressDTO(double locationLat, double locationLng, String placeId){
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.placeId = placeId;
    }
}
