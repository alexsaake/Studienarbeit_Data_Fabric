package com.mse.datafabric.dataProducts.data.charts;

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
    public void addDatasets(DataProductChartsDatasetDTO[] datasets){
        this.datasets = datasets;
    }
}
