package com.mse.datafabric.dataProducts.data.charts;

import com.mse.datafabric.dataProducts.data.DataProductData;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightDataDTO;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightFilter;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightRepository;
import com.mse.datafabric.dataProducts.data.insights.InsightFilterDTO;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;

import java.util.*;

@ShellComponent
public class DataProductCharts {

    private List<Map<String, Object>> data;
    @Autowired
    private DataProductData dataProductData;
    @Autowired
    private DataProductChartRepository chartsRepo;

    public void getData(long id){
        dataProductData.setId(id);
        //
        data = dataProductData.getData();
    }
    public String[] getDataValues(String columnName){
        List<String> values = new ArrayList<>();
        data.forEach(row->{
            Object rowObj = row.get(columnName);
            if(rowObj == null)
                return;
            String value = String.valueOf(rowObj);
            values.add(value);
        });
        return values.toArray(new String[0]);
    }
    public Float[] getDataValuesFloat(String columnName){
        List<Float> values = new ArrayList<>();
        data.forEach(row->{
            Object rowObj = row.get(columnName);
            float value = 0f;
            if(rowObj != null && rowObj != "")
                value = Float.parseFloat(String.valueOf(rowObj).replace(" ",""));
            values.add(value);
        });
        return values.toArray(new Float[0]);
    }
    public List<DataProductChartsDataDTO> sortData(List<DataProductChartsDataDTO> data){
        Comparator<DataProductChartsDataDTO> comparator;
        if (checkForDouble(data))
            comparator = Comparator.comparingDouble(DataProductChartsDataDTO::getXValueD);
        else
            comparator = Comparator.comparing(DataProductChartsDataDTO::getXValueS);
        data.sort(comparator);
        // Liste sortieren
        return data;
    }
    public boolean checkForDouble(List<DataProductChartsDataDTO> data){
        for (DataProductChartsDataDTO dataItem : data) {
            try {
                Double.parseDouble(dataItem.xValue);
            }catch (Exception e){
                return false;
            }
        }
        return true;
    }
    public void setDataValues_YFloat(DataProductChartsDTO chart){
        String[] xValues = getDataValues(chart.xAxisDataproductColumn);
        Float[] yValues = getDataValuesFloat(chart.yAxisDataproductColumn);
        //
        if(xValues.length != yValues.length)
            return;
        //
        List<DataProductChartsDataDTO> data = new ArrayList<>();
        for (int i = 0; i < xValues.length; i++){
            DataProductChartsDataDTO item = dataListContainsXValue(data, xValues[i]);
            if(item == null){
                data.add(new DataProductChartsDataDTO(yValues[i],xValues[i]));
            }else{
                item.addData(yValues[i]);
            }
        }
        //
        data = sortData(data);
        //
        chart.xAxisValues = getDataXValues(data);
        chart.yAxisValues = getDataYValuesF(data);
    }
    public void setDataValues_YCount(DataProductChartsDTO chart){
        String[] xValues = getDataValues(chart.xAxisDataproductColumn);
        //
        List<DataProductChartsDataDTO> data = new ArrayList<>();
        for (int i = 0; i < xValues.length; i++){
            DataProductChartsDataDTO item = dataListContainsXValue(data, xValues[i]);
            if(item == null){
                data.add(new DataProductChartsDataDTO("",xValues[i]));
            }else{
                item.addData("");
            }
        }
        //
        data = sortData(data);
        //
        chart.xAxisValues = getDataXValues(data);
        chart.yAxisValues = getDataYValuesS(data);
    }
    public String[] getDataXValues(List<DataProductChartsDataDTO> data){
        List<String> values = new ArrayList<>();
        for (DataProductChartsDataDTO dataItem : data) {
            values.add(dataItem.xValue);
        }
        return values.toArray(new String[0]);
    }
    public String[] getDataYValuesF(List<DataProductChartsDataDTO> data){
        List<String> values = new ArrayList<>();
        for (DataProductChartsDataDTO dataItem : data) {
            values.add(String.valueOf(dataItem.calcData));
        }
        return values.toArray(new String[0]);
    }
    public String[] getDataYValuesS(List<DataProductChartsDataDTO> data){
        List<String> values = new ArrayList<>();
        for (DataProductChartsDataDTO dataItem : data) {
            values.add(String.valueOf(dataItem.dataS.size()));
        }
        return values.toArray(new String[0]);
    }
    public DataProductChartsDTO[] getChartData(long id){
        DataProductChartsDTO[] charts = chartsRepo.getDataProductCharts(id);
        for (DataProductChartsDTO chart : charts) {
            switch (chart.yValueType){
                case 1:
                    chart.xAxisValues = getDataValues(chart.xAxisDataproductColumn);
                    chart.yAxisValues = getDataValues(chart.yAxisDataproductColumn);
                    break;
                case 2:
                    setDataValues_YFloat(chart);
                    break;
                case 3:
                    setDataValues_YCount(chart);
                    break;
            }
        }
        return charts;
    }
    private DataProductChartsDataDTO dataListContainsXValue(List<DataProductChartsDataDTO> data, String xValue){
        for(DataProductChartsDataDTO dataItem: data){
            if(dataItem.xValue.equals(xValue))
                return dataItem;
        }
        return null;
    }
}
