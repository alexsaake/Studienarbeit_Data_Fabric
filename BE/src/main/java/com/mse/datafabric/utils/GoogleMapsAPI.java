package com.mse.datafabric.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import com.mse.datafabric.dataProducts.models.DataProductSQLWhitelists;
import com.mse.datafabric.immobilien.webScraper.wgGesuchtDE.PageWgGesuchtDe;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.google.maps.model.AddressComponentType.POSTAL_CODE;

@ShellComponent
@Component
public class GoogleMapsAPI {

    private static final String API_HOST = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static final String  API_KEY = "AIzaSyBsdnjk_X8Rb_kgJg26R7JTsJYWiRaoce0";

    GeoApiContext context;
    GeocodingResult[] results;
    public GoogleMapsAPI() {
         context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
         //context.shutdown();
    }
    public void updateDTO(GoogleMapsAddressDTO dto){
        results = getResults(dto.street + ", "+ dto.city);
        String placeId = getPlaceId(results);
        String postalCode = getPostalCode(results);
        double locationLat = getLocationLat(results);
        double locationLng = getLocationLng(results);
        //
        dto.placeId = placeId;
        dto.postalCode = postalCode;
        dto.locationLat = locationLat;
        dto.locationLng = locationLng;
    }
    public GeocodingResult[] getResults(String address){
        GeocodingResult[] results;
        try {
            results = GeocodingApi.geocode(context,
                    address).await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return results;
    }
    public String getPlaceId(GeocodingResult[] results){
        if (results.length < 1)
            return null;
        return results[0].placeId;
    }
    public String getPostalCode(GeocodingResult[] results){
        if (results.length < 1)
            return null;
        AddressComponent[] ac = results[0].addressComponents;
        for(int i = 0;i < ac.length;i++){
            for(int k = 0;k < ac[i].types.length;k++){
                if(ac[i].types[k] == POSTAL_CODE)
                    return ac[i].longName;
            }
        }
        return null;
    }
    public double getLocationLng(GeocodingResult[] results){
        if (results.length < 1 || results[0].geometry == null || results[0].geometry.location == null)
            return 0;
        return results[0].geometry.location.lng;
    }
    public double getLocationLat(GeocodingResult[] results){
        if (results.length < 1 || results[0].geometry == null || results[0].geometry.location == null)
            return 0;
        return results[0].geometry.location.lat;
    }


}
