package com.mse.datafabric.dataProducts.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.dataProducts.DataProductRepository;
import com.mse.datafabric.dataProducts.data.charts.DataProductChartRepository;
import com.mse.datafabric.dataProducts.data.charts.DataProductChartsDTO;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightDataDTO;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightRepository;
import com.mse.datafabric.dataProducts.data.insights.InsightFilterDTO;
import com.mse.datafabric.dataProducts.payload.DataProductAllDTO;
import com.mse.datafabric.dataProducts.models.DataProductDTO;
import com.mse.datafabric.utils.GoogleMapsAPI;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ShellComponent
public class DataProductData {
    @Autowired
    private DataProductRepository dataProductRepository;
    @Autowired
    private DataProductInsightRepository dataProductInsightRepository;
    @Autowired
    private DataProductChartRepository dataProductChartRepository;
    private long id;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;


    public void setId(long id){
        this.id = id;
    }

    public List<Map<String, Object>> getData(){
        try {
            return objectMapper.readValue(dataProductRepository.getData(id), new TypeReference<List<Map<String, Object>>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public int dataProductAddMapsData(){
        int count = 0;
        GoogleMapsAddressDTO addressColumns = dataProductRepository.getAddressColumns(id);
        if(addressColumns == null)
            return 0;
        //
        List<Map<String, Object>> data = getData();
        //
        for (Map<String, Object> dataRow : data) {
            if (dataContainsMapsData(dataRow))
                continue;
            String city = String.valueOf(dataRow.get(addressColumns.city));
            String street = String.valueOf(dataRow.get(addressColumns.street));
            //
            GoogleMapsAddressDTO dto = new GoogleMapsAddressDTO(city, street);
            googleMapsAPI.updateDTO(dto);
            dataRow.put("_mapsData", dto);
            count++;
        }
        //
        try {
            dataProductRepository.setData(id,objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
    public long createDataProduct(DataProductAllDTO dto){
        long id = dataProductRepository.insertDataProduct(dto.metaData);
        createInsightData(dto, id);
        createChartData(dto, id);
        //
        return id;
    }
    private void createChartData(DataProductAllDTO dto, long id){
        dataProductChartRepository.insertCharts(id,dto.chartData);
    }
    private void createInsightData(DataProductAllDTO dto, long id){
        dataProductInsightRepository.insertInsights(id,dto.insights);
        dataProductInsightRepository.insertInsightsFilter(id,dto.insightFilters);
        if(dto.mapsData.city != null && !dto.mapsData.city.equals("") && dto.mapsData.street != null && !dto.mapsData.street.equals("")) {
            dataProductRepository.setAddressColumns(id, dto.mapsData);
            setId(id);
            dataProductAddMapsData();
        }
    }
    public boolean editDataProduct(DataProductAllDTO dto, long dataProductId){
        dataProductRepository.updateDataProduct(dto.metaData, dataProductId);
        dataProductInsightRepository.deleteInsights(dataProductId);
        dataProductInsightRepository.deleteInsightsFilter(dataProductId);
        dataProductRepository.deleteAddressColumns(dataProductId);
        dataProductChartRepository.deleteCharts(dataProductId);
        //
        createInsightData(dto, dataProductId);
        createChartData(dto, dataProductId);
        return true;
    }
    public DataProductAllDTO getDataProductAll(long dataProductId){
        DataProductDTO dataProduct = dataProductRepository.getDataProduct(dataProductId);
        DataProductInsightDataDTO[] insightData = dataProductInsightRepository.getInsightsData(dataProductId);
        InsightFilterDTO[] insightFilter = dataProductInsightRepository.getInsightFiltersData(dataProductId);
        GoogleMapsAddressDTO mapsData = dataProductRepository.getMapsData(dataProductId);
        DataProductChartsDTO[] chartData = dataProductChartRepository.getDataProductCharts(dataProductId);
        //
        return new DataProductAllDTO(dataProduct,insightData,insightFilter,mapsData,chartData);
    }

    private boolean dataContainsMapsData(Map<String, Object> dataRow){
        if(dataRow.containsKey("_mapsData"))
            return true;
        return false;
    }
}
