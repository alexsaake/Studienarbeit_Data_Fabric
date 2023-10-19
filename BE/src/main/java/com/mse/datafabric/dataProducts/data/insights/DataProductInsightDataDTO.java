package com.mse.datafabric.dataProducts.data.insights;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataProductInsightDataDTO {
    public float insightValue;
    public String displayName;
    public String unit;
    public String dataProductColumn;

    public int type;

    public String shortkey;


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
    @JsonCreator
    public DataProductInsightDataDTO(@JsonProperty("shortKey")String shortkey, @JsonProperty("displayName")String displayName,
                            @JsonProperty("dataProductColumn")String dataProductColumn, @JsonProperty("insightType")int type,
                            @JsonProperty("unit")String unit){
        this.shortkey = shortkey;
        this.displayName = displayName;
        this.dataProductColumn =  dataProductColumn;
        this.type =  type;
        this.unit =  unit;
    }
    public void setValue(float insightValue){
        this.insightValue = insightValue;
    }
}
