package com.mse.datafabric.dataProducts.data.insights;

public class DataProductInsightDataDTO {
    public float insightValue;
    public String displayName;
    public String unit;
    public String dataProductColumn;

    public int type;


    public DataProductInsightDataDTO(float insightValue, String displayName, String unit){
        this.insightValue = insightValue;
        this.displayName = displayName;
        this.unit = unit;
    }
    public DataProductInsightDataDTO(int type, String displayName, String unit, String dataProductColumn){
        this.type = type;
        this.dataProductColumn = dataProductColumn;
        this.displayName = displayName;
        this.unit = unit;
    }
    public void setValue(float insightValue){
        this.insightValue = insightValue;
    }
}
