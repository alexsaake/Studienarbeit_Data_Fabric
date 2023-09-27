package com.mse.datafabric.dataProducts.data.insights;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataProductInsightFilter {

    DataProductInsightRepository dataProductInsightRepository;
    public String[] filterKeys;
    public String[] filterValues;

    public String shortkey;
    public InsightFilterDTO[] activeFilters;


    public DataProductInsightFilter(String filterKeys, String filterValues, String shortkey, DataProductInsightRepository dataProductInsightRepository){
        if(filterKeys == null)
            this.filterKeys = new String[]{};
        else
            this.filterKeys = filterKeys.split(";");
        if(filterValues == null)
            this.filterValues = new String[]{};
        else
            this.filterValues = filterValues.split(";");
        this.shortkey = shortkey;
        this.dataProductInsightRepository = dataProductInsightRepository;
        //
        setActiveFilters();
    }

    private void setActiveFilters(){
        List<InsightFilterDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < filterKeys.length; i++) {
            String filterColumn = dataProductInsightRepository.getFilterColumnById(Integer.parseInt(filterKeys[i]));
            int filterType = dataProductInsightRepository.getFilterTypeById(Integer.parseInt(filterKeys[i]));
            if(filterColumn == null || filterType == 0)
                continue;
            InsightFilterDTO dto = new InsightFilterDTO(null, filterType, -1);
            dto.setFilterColumn(filterColumn);
            dtoList.add(dto);
        }
        activeFilters = dtoList.toArray(new InsightFilterDTO[0]);
    }

    public boolean filterOutRow(Map<String, Object> dataRow){
        for (int i = 0; i < activeFilters.length; i++) {
            if(!checkFilterValues(dataRow, activeFilters[i].filterColumn, activeFilters[i].filterType, filterValues[i].split(",")))
                return true;
        }
        return false;
    }
    private String getValue(Map<String, Object> dataRow, String filterColumn){
        return String.valueOf(dataRow.get(filterColumn));
    }
    public boolean checkFilterValues(Map<String, Object> dataRow, String filterColumn, int filterType, String[] filterValues){
        for (int i = 0; i < filterValues.length; i++) {
            if(getValue(dataRow, filterColumn).equals(filterValues[i]))
                return true;
        }
        return false;
    }

    public String getSQLFilter(String shortkey, String statement){
        String prefix = " WHERE ";
        if(statement.toUpperCase().contains("WHERE"))
            prefix = " AND ";

        if(filterKeys.length != filterValues.length)
            return null;
        String sqlFilter = "";
        int count = 0;
        for (int i = 0; i < filterKeys.length; i++) {
            String filterColumn = dataProductInsightRepository.getFilterColumnById(Integer.parseInt(filterKeys[i]));
            int filterType = dataProductInsightRepository.getFilterTypeById(Integer.parseInt(filterKeys[i]));
            if(filterColumn == null || filterType == 0)
                continue;
            String filter = getFilter(shortkey, filterColumn, filterType, filterValues[i]);
            if(!filter.equals("")){
                if (count > 0)
                    sqlFilter += " AND ";
                count++;
            }
            sqlFilter += filter;

        }
        return (sqlFilter.equals("") ? "" : prefix + sqlFilter);
    }
    private String getFilter(String shortKey, String filterColumn, int filterType, String filterValue){
        String sqlFilter = "";
        String[] splits = filterValue.split(",");
        switch (filterType){
            case 2:
                sqlFilter ="google_maps_data."+filterColumn +" IN ('";
            case 3:
                if(sqlFilter.equals(""))
                    sqlFilter =shortKey+"."+filterColumn +" IN ('";
                for (int i = 0;i< splits.length;i++)
                {
                    if(i > 0)
                        sqlFilter += "','";
                    sqlFilter += splits[i];
                }
                sqlFilter += "')";
                break;
            case 1:
                if(splits.length > 0 && !splits[0].equals("null"))
                    sqlFilter = shortKey+"."+filterColumn + " >= CAST('"+splits[0]+"' AS DATE)";
                if(splits.length > 1 && !splits[1].equals("null")){
                    if(!splits[0].equals("null"))
                        sqlFilter += " AND ";
                    sqlFilter += shortKey+"."+filterColumn + " <= CAST('"+splits[1]+"' AS DATE)";
                }
                break;
        }
        return sqlFilter;
    }
}
