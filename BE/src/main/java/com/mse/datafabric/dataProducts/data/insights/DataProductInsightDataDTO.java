package com.mse.datafabric.dataProducts.data.insights;

public class DataProductInsightDataDTO {
    public float insightValue;
    public String displayName;
    public String unit;

    public DataProductInsightDataDTO(float insightValue, String displayName, String unit){
        this.insightValue = insightValue;
        this.displayName = displayName;
        this.unit = unit;
    }
}
