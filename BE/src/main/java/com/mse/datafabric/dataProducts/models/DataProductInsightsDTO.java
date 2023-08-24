package com.mse.datafabric.dataProducts.models;

import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;

public class DataProductInsightsDTO {
    public float averageRent;
    public float activeItemsCount;
    public float averageSize;
    public float highestRent;
    public float lowestRent;
    public float medianRent;
    public float quartile25Rent;
    public float quartile75Rent;
    public GoogleMapsAddressDTO[] mapsData;
}
