package com.mse.datafabric.dataProducts.data.charts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DataProductChartsDTO {
    public int id;

    public int type;
    public String xAxisName;
    public String yAxisName;
    public String xAxisUnit;
    public String yAxisUnit;
    public String xAxisDataproductColumn;
    public long dataproductId;
    public String displayName;
    public int yValueType;
    public int fillChart;

    public DataProductChartsDatasetDTO[] datasets;

    public String[] xAxisValues;

    public DataProductChartsDTO(int id, int type, String xAxisName, String yAxisName,
                             String xAxisUnit, String yAxisUnit, String xAxisDataproductColumn,
                                String displayName, int yValueType, int fillChart){
        this.id = id;
        this.type = type;
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        this.xAxisUnit = xAxisUnit;
        this.yAxisUnit = yAxisUnit;
        this.xAxisDataproductColumn = xAxisDataproductColumn;
        this.displayName = displayName;
        this.yValueType = yValueType;
        this.fillChart = fillChart;
    }
    @JsonCreator
    public DataProductChartsDTO(@JsonProperty("id")int id, @JsonProperty("chartType")int type, @JsonProperty("xAxisName")String xAxisName, @JsonProperty("yAxisName")String yAxisName,
                                @JsonProperty("xAxisUnit")String xAxisUnit, @JsonProperty("yAxisUnit")String yAxisUnit, @JsonProperty("xAxisDataproductColumn")String xAxisDataproductColumn,
                                @JsonProperty("displayName")String displayName, @JsonProperty("yValueType")int yValueType, @JsonProperty("fillChart")int fillChart, @JsonProperty("datasets")DataProductChartsDatasetDTO[] datasets){
        this.id = id;
        this.type = type;
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        this.xAxisUnit = xAxisUnit;
        this.yAxisUnit = yAxisUnit;
        this.xAxisDataproductColumn = xAxisDataproductColumn;
        this.displayName = displayName;
        this.yValueType = yValueType;
        this.fillChart = fillChart;
        this.datasets = datasets;
    }
    public void addDatasets(DataProductChartsDatasetDTO[] datasets){
        this.datasets = datasets;
    }
}
