package com.mse.datafabric.dataProducts.data.insights;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataProductInsightFilter {
    DataProductInsightRepository dataProductInsightRepository;
    public String[] filterKeys;
    public String[] filterValues;
    public long id;
    public InsightFilterDTO[] activeFilters;


    public DataProductInsightFilter(String filterKeys, String filterValues, long id, DataProductInsightRepository dataProductInsightRepository){
        if(filterKeys == null)
            this.filterKeys = new String[]{};
        else
            this.filterKeys = filterKeys.split(";");
        if(filterValues == null)
            this.filterValues = new String[]{};
        else
            this.filterValues = filterValues.split(";");
        this.id = id;
        this.dataProductInsightRepository = dataProductInsightRepository;
        //
        setActiveFilters();
    }

    private void setActiveFilters(){
        List<InsightFilterDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < filterKeys.length; i++) {

            InsightFilterDTO dto = dataProductInsightRepository.getFilterById(id,Integer.parseInt(filterKeys[i]));
            if(dto == null || dto.filterColumn == null || dto.filterType == 0)
                continue;
            dto.setFilterColumn(dto.filterColumn);
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
    private String getMapsValue(Map<String, Object> dataRow, String filterColumn){
        Map<String, Object> mapsData = (Map<String, Object>) dataRow.get("_mapsData");
        return String.valueOf(mapsData.get(filterColumn));
    }
    private boolean checkDates(Map<String, Object> dataRow, String filterColumn, String[] filterValues){
        if(filterValues.length != 2)
            return false;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(getValue(dataRow, filterColumn));
            Date dateFrom = null;
            Date dateTo = null;

            if(!filterValues[0].equals("null")){
                dateFrom = formatter.parse(filterValues[0]);
            }
            if(!filterValues[1].equals("null")){
                dateTo = formatter.parse(filterValues[1]);
            }
            //
            if(dateFrom != null && dateTo == null)
                return date.after(dateFrom) || date.equals(dateFrom);
            if(dateTo != null && dateFrom == null)
                return date.before(dateTo) || date.equals(dateTo);
            return (date.after(dateFrom) || date.equals(dateFrom)) && (date.before(dateTo) || date.equals(dateTo));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean checkFilterValues(Map<String, Object> dataRow, String filterColumn, int filterType, String[] filterValues){
        for (int i = 0; i < filterValues.length; i++) {
            switch (filterType){
                case 1:
                        return checkDates(dataRow, filterColumn, filterValues);
                case 2:
                    if(getMapsValue(dataRow, filterColumn).equals(filterValues[i]))
                        return true;
                    break;
                case 3:
                    if(getValue(dataRow, filterColumn).equals(filterValues[i]))
                        return true;
                    break;
            }
        }
        return false;
    }
}
