package com.mse.datafabric.utils.dtos;

public class GoogleMapsAddressDTO {
    public int dataId;

    public String city;
    public String street;
    public String placeId;
    public String postalCode;
    public double locationLat;
    public double locationLng;
    public GoogleMapsAddressDTO(String city, String street){
        this.city = city;
        this.street = street;
        this.dataId = -1;
    }
    public GoogleMapsAddressDTO(double locationLat, double locationLng){
        this.locationLat = locationLat;
        this.locationLng = locationLng;
    }
}
