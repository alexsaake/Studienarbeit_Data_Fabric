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
    public DataProductChartsDTO[] getChartData(long id){
        DataProductChartsDTO[] charts = chartsRepo.getDataProductCharts(id);
        for (DataProductChartsDTO chart : charts) {
            chart.xAxisValues = getDataValues(chart.xAxisDataproductColumn);
            chart.yAxisValues = getDataValues(chart.yAxisDataproductColumn);
        }
        return charts;
    }
}
