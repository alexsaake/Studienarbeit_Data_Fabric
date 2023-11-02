package com.mse.datafabric.dataProducts.data.insights;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InsightFilterDTO {
    public String displayName;
    public int filterType;
    public int filterId;
    public String filterColumn;
    public int id;

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
    public InsightFilterDTO(@JsonProperty("id")int id, @JsonProperty("displayName")String displayName,
                          @JsonProperty("dataProductColumn")String filterColumn, @JsonProperty("filterType")int filterType){
        this.id = id;
        this.displayName = displayName;
        this.filterColumn =  filterColumn;
        this.filterType =  filterType;
    }
    public void setFilterColumn(String filterColumn){
        this.filterColumn =  filterColumn;
    }
}
