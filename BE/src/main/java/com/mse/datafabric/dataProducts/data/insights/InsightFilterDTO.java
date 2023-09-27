package com.mse.datafabric.dataProducts.data.insights;

public class InsightFilterDTO {
    public String displayName;
    public int filterType;
    public int filterId;
    public String filterColumn;

    public InsightFilterDTO(String displayName, int filterType, int filterId){
        this.displayName = displayName;
        this.filterType = filterType;
        this.filterId = filterId;
    }
    public void setFilterColumn(String filterColumn){
        this.filterColumn =  filterColumn;
    }
}
