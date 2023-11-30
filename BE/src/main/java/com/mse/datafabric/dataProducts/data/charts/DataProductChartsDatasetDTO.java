package com.mse.datafabric.dataProducts.data.charts;

public class DataProductChartsDatasetDTO {
    public int dataproductChartsId;
    public String displayName;
    public String yAxisDataproductColumn;
    public String[] datasetValues;

    public DataProductChartsDatasetDTO(int dataproductChartsId, String displayName, String yAxisDataproductColumn){
        this.dataproductChartsId = dataproductChartsId;
        this.displayName = displayName;
        this.yAxisDataproductColumn = yAxisDataproductColumn;
    }
}
