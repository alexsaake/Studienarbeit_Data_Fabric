package com.mse.datafabric.dataProducts.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightDataDTO;
import com.mse.datafabric.dataProducts.data.insights.InsightFilterDTO;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;

import java.util.Date;

public class DataProductAllDTO {
    public DataProductDTO metaData;
    public DataProductInsightDataDTO[] insights;
    public InsightFilterDTO[] insightFilters;
    public GoogleMapsAddressDTO mapsData;
    @JsonCreator
    public DataProductAllDTO(@JsonProperty("metaData")DataProductDTO dataProductData, @JsonProperty("insights")DataProductInsightDataDTO[] insights,
                             @JsonProperty("filter")InsightFilterDTO[] insightFilters, @JsonProperty("mapsData")GoogleMapsAddressDTO mapsData) {
        this.metaData = dataProductData;
        this.insights = insights;
        this.insightFilters = insightFilters;
        this.mapsData = mapsData;
    }
}
