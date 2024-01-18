package com.mse.datafabric.dataProducts.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mse.datafabric.dataProducts.data.charts.DataProductChartsDTO;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightDataDTO;
import com.mse.datafabric.dataProducts.data.insights.InsightFilterDTO;
import com.mse.datafabric.dataProducts.models.DataProductDTO;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;

public class DataProductAllDTO {
    public DataProductDTO metaData;
    public DataProductInsightDataDTO[] insights;
    public InsightFilterDTO[] insightFilters;
    public GoogleMapsAddressDTO mapsData;
    public DataProductChartsDTO[] chartData;
    @JsonCreator
    public DataProductAllDTO(@JsonProperty("metaData")DataProductDTO dataProductData, @JsonProperty("insights")DataProductInsightDataDTO[] insights,
                             @JsonProperty("filter")InsightFilterDTO[] insightFilters, @JsonProperty("mapsData")GoogleMapsAddressDTO mapsData, @JsonProperty("chartData")DataProductChartsDTO[] chartData) {
        this.metaData = dataProductData;
        this.insights = insights;
        this.insightFilters = insightFilters;
        this.mapsData = mapsData;
        this.chartData = chartData;
    }
}
