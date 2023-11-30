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
    public String[] getDataValuesS(String columnName){
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
    public Float[] getDataValuesF(String columnName){
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
    public List<String[]> getDatasetValuesS(DataProductChartsDTO chart, String[] xValues){
        List<String[]> values = new ArrayList<>();
        for (DataProductChartsDatasetDTO dataset : chart.datasets) {
            values.add(getDataValuesS(dataset.yAxisDataproductColumn));
        }
        return values;
    }
    public List<Float[]> getDatasetValuesF(DataProductChartsDTO chart){
        List<Float[]> values = new ArrayList<>();
        for (DataProductChartsDatasetDTO dataset : chart.datasets) {
            values.add(getDataValuesF(dataset.yAxisDataproductColumn));
        }
        return values;
    }
    public List<DataProductChartsDataDTO> sortData(List<DataProductChartsDataDTO> data){
        Comparator<DataProductChartsDataDTO> comparator;
        if (checkForDouble(data))
            comparator = Comparator.comparingDouble(DataProductChartsDataDTO::getXValueD);
        else
            comparator = Comparator.comparing(DataProductChartsDataDTO::getXValueS);
        data.sort(comparator);
        return data;
    }
    public boolean checkForDouble(List<DataProductChartsDataDTO> data){
        List<DataProductChartsDataDTO> removeData = new ArrayList<>();
        for (DataProductChartsDataDTO dataItem : data) {
            try {
                if(dataItem.xValue.equals("")) {
                    removeData.add(dataItem);
                    continue;
                }
                Double.parseDouble(dataItem.xValue);
            }catch (Exception e){
                return false;
            }
        }
        for (DataProductChartsDataDTO removeDataItem : removeData) {
            data.remove(removeDataItem);
        }
        return true;
    }
    public void setDataValues_Y(DataProductChartsDTO chart){
        String[] xValues = getDataValuesS(chart.xAxisDataproductColumn);
        List<String[]> datasetValuesS = getDatasetValuesS(chart, xValues);
        //
        for (int i = 0; i < chart.datasets.length; i++){
            List<DataProductChartsDataDTO> data = new ArrayList<>();
            for(int j = 0; j < datasetValuesS.get(i).length; j++) {
                DataProductChartsDataDTO item = new DataProductChartsDataDTO(xValues[j]);
                item.addDataS(datasetValuesS.get(i)[j]);
                data.add(item);
            }
            data = sortData(data);
            //
            chart.xAxisValues = getDataXValues(data);
            chart.datasets[i].datasetValues = getDataYValuesS(data);
        }
    }
    public void setDataValues_YFloat(DataProductChartsDTO chart){
        String[] xValues = getDataValuesS(chart.xAxisDataproductColumn);
        List<Float[]> datasetValuesF = getDatasetValuesF(chart);
        //
        for (int i = 0; i < chart.datasets.length; i++){
            List<DataProductChartsDataDTO> data = new ArrayList<>();
            for(int j = 0; j < datasetValuesF.get(i).length; j++) {
                DataProductChartsDataDTO item = dataListContainsXValue(data, xValues[j]);
                if (item == null) {
                    item = new DataProductChartsDataDTO(xValues[j]);
                    data.add(item);
                }
                item.addDataF(datasetValuesF.get(i)[j]);
            }
            data = sortData(data);
            //
            chart.xAxisValues = getDataXValues(data);
            chart.datasets[i].datasetValues = getDataYValuesF(data);
        }
    }
    public void setDataValues_YCount(DataProductChartsDTO chart){
        String[] xValues = getDataValuesS(chart.xAxisDataproductColumn);
        //
        for (int i = 0; i < chart.datasets.length; i++) {
            List<DataProductChartsDataDTO> data = new ArrayList<>();
            for (String xValue : xValues) {
                DataProductChartsDataDTO item = dataListContainsXValue(data, xValue);
                if (item == null) {
                    item = new DataProductChartsDataDTO(xValue);
                    data.add(item);
                }
                item.addDataS("");
            }
            //
            data = sortData(data);
            //
            chart.xAxisValues = getDataXValues(data);
            chart.datasets[i].datasetValues = getDataYValues_Count(data);
        }
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
            values.add(String.valueOf(dataItem.dataS.get(0)));
        }
        return values.toArray(new String[0]);
    }
    public String[] getDataYValues_Count(List<DataProductChartsDataDTO> data){
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
                    setDataValues_Y(chart);
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
