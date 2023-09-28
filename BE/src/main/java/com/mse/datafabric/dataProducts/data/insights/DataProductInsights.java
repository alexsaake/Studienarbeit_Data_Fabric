package com.mse.datafabric.dataProducts.data.insights;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.dataProducts.data.DataProductData;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.shell.standard.ShellComponent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ShellComponent
public class DataProductInsights {

    private List<Map<String, Object>> data;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataProductData dataProductData;
    private DataProductInsightFilter filter;
    @Autowired
    private DataProductInsightRepository insightsRepo;

    public void getData(String shortkey, DataProductInsightFilter filter){
        this.filter = filter;
        //
        dataProductData.setShortkey(shortkey);
        //
        data = dataProductData.getData();
        filterData();
    }
    public void filterData(){
        List<Map<String, Object>> filteredDataList = new ArrayList<>();
        data.forEach(row->{
            if(!filter.filterOutRow(row))
                filteredDataList.add(row);
        });
        //
        data = filteredDataList;
    }
    public List<Float> getDataValues(String columnName){
        List<Float> values = new ArrayList<>();
        data.forEach(row->{
            Object rowObj = row.get(columnName);
            if(rowObj == null)
                return;
            Float value = Float.valueOf(String.valueOf(rowObj).replace(" ",""));
            values.add(value);
        });
        return values;
    }
    public String[] getDifferentColumnValues(String shortkey, int filterId) {
        InsightFilterDTO filterData = insightsRepo.getFilterById(shortkey, filterId);
        if(filterData == null)
            return null;
        //
        List<String> differentValues = new ArrayList<>();
        List<Map<String, Object>> data = dataProductData.getData();
        for (Map<String, Object> dataRow : data) {
            String value = "";
            switch (filterData.filterType){
                case 2:
                    Map<String, Object> mapsData = (Map<String, Object>) dataRow.get("_mapsData");
                    value = String.valueOf(mapsData.get(filterData.filterColumn));
                    break;
                case 3:
                    value = String.valueOf(dataRow.get(filterData.filterColumn));
                    break;
            }
            if(!differentValues.contains(value))
                differentValues.add(value);
        }
        return differentValues.toArray(new String[0]);
    }
    public DataProductInsightDataDTO[] getInsights(String shortkey){
        DataProductInsightDataDTO[] dtoList = insightsRepo.getDataProductInsights(shortkey);
        for (DataProductInsightDataDTO dto : dtoList) {
            dto.setValue(getInsightValue(dto.type,shortkey,dto.dataProductColumn));
        }
        return dtoList;
    }

    private float getInsightValue(int type, String shortkey, String columnName){
        switch (type){
            case 1:
                return getCount(columnName);
            case 2:
                return getAverage(columnName);
            case 3:
                return 0;
            case 4:
                return 0;
            case 5:
                return 0;
            case 6:
                return 0;
            case 7:
                return 0;
        }
        return 0;
    }

    public float getAverage(String columnName){
        float retValue = 0;
        List<Float> values =  getDataValues(columnName);
        for (Float value : values) {
            retValue += value;
        }
        retValue= retValue/values.size();
        retValue = (float) (Math.round(retValue * 100.0) / 100.0);
        return retValue;
    }
    public float getCount(String columnName){
        List<Float> values =  getDataValues(columnName);
        return values.size();
    }

    public GoogleMapsAddressDTO[] getInsightMapsData(String shortkey, DataProductInsightFilter filter) {
        List<GoogleMapsAddressDTO> dtoLlist = new ArrayList<>();
        data.forEach(dataRow->{
            Map<String, Object> mapsData = (Map<String, Object>) dataRow.get("_mapsData");
            if(mapsData == null)
                return;
            Double locationLat = (Double) mapsData.get("locationLat");
            Double locationLng = (Double) mapsData.get("locationLng");
            dtoLlist.add(new GoogleMapsAddressDTO(locationLat, locationLng));
        });
        return dtoLlist.toArray(new GoogleMapsAddressDTO[0]);
    }
}
