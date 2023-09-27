package com.mse.datafabric.dataProducts.data.insights;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.dataProducts.data.DataProductData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
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
            Float value = Float.valueOf(String.valueOf(rowObj));
            values.add(value);
        });
        return values;
    }

    public float getAverage(String columnName){
        float retValue = 0;
        List<Float> values =  getDataValues(columnName);
        for (Float value : values) {
            retValue += value;
        }
        retValue= retValue/values.size();
        return retValue;
    }
    public float getCount(String columnName){
        List<Float> values =  getDataValues(columnName);
        return values.size();
    }
}
