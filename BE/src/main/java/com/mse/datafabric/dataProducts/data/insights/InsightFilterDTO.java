package com.mse.datafabric.dataProducts.data.insights;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InsightFilterDTO {
    public String displayName;
    public int filterType;
    public int filterId;
    public String filterColumn;
    public String shortkey;

    public InsightFilterDTO(String displayName, int filterType, int filterId){
        this.displayName = displayName;
        this.filterType = filterType;
        this.filterId = filterId;
    }
    public InsightFilterDTO(String filterColumn, int filterType){
        this.filterColumn =  filterColumn;
        this.filterType =  filterType;
    }

    @JsonCreator
    public InsightFilterDTO(@JsonProperty("shortKey")String shortkey, @JsonProperty("displayName")String displayName,
                          @JsonProperty("dataProductColumn")String filterColumn, @JsonProperty("filterType")int filterType){
        this.shortkey = shortkey;
        this.displayName = displayName;
        this.filterColumn =  filterColumn;
        this.filterType =  filterType;
    }
    public void setFilterColumn(String filterColumn){
        this.filterColumn =  filterColumn;
    }
}
