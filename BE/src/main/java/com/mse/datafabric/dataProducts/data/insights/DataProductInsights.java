package com.mse.datafabric.dataProducts.data.insights;

import com.mse.datafabric.dataProducts.data.DataProductData;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;

import java.util.*;

@ShellComponent
public class DataProductInsights {

    private List<Map<String, Object>> data;
    @Autowired
    private DataProductData dataProductData;
    private DataProductInsightFilter filter;
    @Autowired
    private DataProductInsightRepository insightsRepo;

    public void getData(long id, DataProductInsightFilter filter){
        this.filter = filter;
        //
        dataProductData.setId(id);
        //
        data = dataProductData.getData();
        filterData();
    }
    public int getDataCount(){
        return data.size();
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
            if(rowObj == null || rowObj == "")
                return;
            Float value = Float.valueOf(String.valueOf(rowObj).replace(" ",""));
            values.add(value);
        });
        return values;
    }
    public String[] getDifferentColumnValues(long id, int filterId) {
        InsightFilterDTO filterData = insightsRepo.getFilterById(id, filterId);
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
    public DataProductInsightDataDTO[] getInsights(long id){
        DataProductInsightDataDTO[] dtoList = insightsRepo.getDataProductInsights(id);
        for (DataProductInsightDataDTO dto : dtoList) {
            dto.setValue(getInsightValue(dto.type,dto.dataProductColumn));
        }
        return dtoList;
    }

    private float getInsightValue(int type, String columnName){
        switch (type){
            case 1:
                return getCount(columnName);
            case 2:
                return getAverage(columnName);
            case 3:
                return getHighest(columnName);
            case 4:
                return getLowest(columnName);
            case 5:
                return getMedian(columnName);
            case 6:
                return getQuartile25(columnName);
            case 7:
                return getQuartile75(columnName);
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
    public float getHighest(String columnName){
        List<Float> values =  getDataValues(columnName);
        if(values.size() == 0)
            return 0;
        return Collections.max(values);
    }
    public float getLowest(String columnName){
        List<Float> values =  getDataValues(columnName);
        if(values.size() == 0)
            return 0;
        return Collections.min(values);
    }

    public float getMedian(String columnName){
        List<Float> values =  getDataValues(columnName);
        return 0;
    }
    public float getQuartile25(String columnName){
        List<Float> values =  getDataValues(columnName);
        return 0;
    }
    public float getQuartile75(String columnName){
        List<Float> values =  getDataValues(columnName);
        return 0;
    }

    public float[] Quartiles(List<Float> values) {
        float[] val = new float[values.size()];
        int i = 0;

        for (Float f : values) {
            val[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        float ans[] = new float[3];

        for (int quartileType = 1; quartileType < 4; quartileType++) {
            float length = val.length + 1;
            float quartile;
            float newArraySize = (length * ((float) (quartileType) * 25 / 100)) - 1;
            Arrays.sort(val);
            if (newArraySize % 1 == 0) {
                quartile = val[(int) (newArraySize)];
            } else {
                int newArraySize1 = (int) (newArraySize);
                quartile = (val[newArraySize1] + val[newArraySize1 + 1]) / 2;
            }
            ans[quartileType - 1] =  quartile;
        }
        return ans;
    }
    public GoogleMapsAddressDTO[] getInsightMapsData() {
        List<GoogleMapsAddressDTO> dtoLlist = new ArrayList<>();
        data.forEach(dataRow->{
            Map<String, Object> mapsData = (Map<String, Object>) dataRow.get("_mapsData");
            if(mapsData == null)
                return;
            Double locationLat = (Double) mapsData.get("locationLat");
            Double locationLng = (Double) mapsData.get("locationLng");
            String placeId = (String) mapsData.get("placeId");
            dtoLlist.add(new GoogleMapsAddressDTO(locationLat, locationLng, placeId));
        });
        return dtoLlist.toArray(new GoogleMapsAddressDTO[0]);
    }
}
