package com.mse.datafabric.dataProducts.models;

public class DataProductSQLFilterDTO {
    public String areaFilter;
    public String dateFromFilter;
    public String dateToFilter;

    public DataProductSQLFilterDTO(String areaFilter,String dateFromFilter,String dateToFilter){
        this.areaFilter = areaFilter;
        this.dateFromFilter = dateFromFilter;
        this.dateToFilter = dateToFilter;
    }
    public String[] getFilterValues(){
        return new String[]{areaFilter,dateFromFilter,dateToFilter};
    }
    public String getFilterFromColumns(DataProductSQLFilterDTO filterValues) {
        int count = 0;
        if (filterValues.areaFilter == null && filterValues.dateFromFilter == null && filterValues.dateToFilter == null)
            return "";
        String sqlFilter = " ";
        if (filterValues.areaFilter != null) {
            sqlFilter += areaFilter + " IN (";
            String[] splits = filterValues.areaFilter.split(",");
            for (int i = 0;i< splits.length;i++)
            {
                if(i > 0)
                    sqlFilter += ",";
                sqlFilter += "?";
            }
            sqlFilter += ")";
            count++;
        }
        if (filterValues.dateFromFilter != null) {
            if (count > 0)
                sqlFilter += " AND ";
            sqlFilter += dateFromFilter + " >= ?";;
            count++;
        }
        if (filterValues.dateToFilter != null) {
            if (count > 0)
                sqlFilter += " AND ";
            sqlFilter += dateToFilter + " <= ?";;
        }
        //
        return sqlFilter;
    }
}
