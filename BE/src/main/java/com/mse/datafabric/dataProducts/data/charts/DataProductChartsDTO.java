package com.mse.datafabric.dataProducts.data.charts;

import com.mse.datafabric.dataProducts.data.insights.DataProductInsightDataDTO;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;

public class DataProductChartsDTO {
    public int type;
    public String xAxisName;
    public String yAxisName;
    public String xAxisUnit;
    public String yAxisUnit;
    public String xAxisDataproductColumn;
    public String yAxisDataproductColumn;
    public long dataproductId;
    public String displayName;
    public String[] xAxisValues;
    public String[] yAxisValues;

    public DataProductChartsDTO(int type, String xAxisName, String yAxisName,
                             String xAxisUnit, String yAxisUnit, String xAxisDataproductColumn,
                             String yAxisDataproductColumn, String displayName){
        this.type = type;
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        this.xAxisUnit = xAxisUnit;
        this.yAxisUnit = yAxisUnit;
        this.xAxisDataproductColumn = xAxisDataproductColumn;
        this.yAxisDataproductColumn = yAxisDataproductColumn;
        this.displayName = displayName;
    }
}
