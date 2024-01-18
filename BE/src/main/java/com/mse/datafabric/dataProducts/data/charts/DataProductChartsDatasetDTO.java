package com.mse.datafabric.dataProducts.data.charts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataProductChartsDatasetDTO {
    public int dataproductChartsId;
    public String displayName;
    public String yAxisDataproductColumn;
    public String[] datasetValues;
    @JsonCreator
    public DataProductChartsDatasetDTO(@JsonProperty("id")int dataproductChartsId, @JsonProperty("displayName")String displayName, @JsonProperty("yAxisDataproductColumn")String yAxisDataproductColumn){
        this.dataproductChartsId = dataproductChartsId;
        this.displayName = displayName;
        this.yAxisDataproductColumn = yAxisDataproductColumn;
    }
}
