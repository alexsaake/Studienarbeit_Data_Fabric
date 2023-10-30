package com.mse.datafabric.dataProducts.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.dataProducts.DataProductRepository;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightDataDTO;
import com.mse.datafabric.dataProducts.data.insights.DataProductInsightRepository;
import com.mse.datafabric.dataProducts.data.insights.InsightFilterDTO;
import com.mse.datafabric.dataProducts.models.DataProductAllDTO;
import com.mse.datafabric.dataProducts.models.DataProductDTO;
import com.mse.datafabric.utils.GoogleMapsAPI;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ShellComponent
public class DataProductData {
    @Autowired
    private DataProductRepository dataProductRepository;
    @Autowired
    private DataProductInsightRepository dataProductInsightRepository;
    private String shortkey;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;


    public void setShortkey(String shortkey){
        this.shortkey = shortkey;
    }

    public List<Map<String, Object>> getData(){
        try {
            return objectMapper.readValue(dataProductRepository.getData(shortkey), new TypeReference<List<Map<String, Object>>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public int dataProductAddMapsData(){
        int count = 0;
        GoogleMapsAddressDTO addressColumns = dataProductRepository.getAddressColumns(shortkey);
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
            dataProductRepository.setData(shortkey,objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
    public String createDataProduct(DataProductAllDTO dto){
        String shortKey = dataProductRepository.insertDataProduct(dto.metaData);
        createInsightData(dto, shortKey);
        //
        return shortKey;
    }
    private void createInsightData(DataProductAllDTO dto, String shortKey){
        dataProductInsightRepository.insertInsights(shortKey,dto.insights);
        dataProductInsightRepository.insertInsightsFilter(shortKey,dto.insightFilters);
        if(dto.mapsData.city != null && !dto.mapsData.city.equals("") && dto.mapsData.street != null && !dto.mapsData.street.equals("")) {
            dataProductRepository.setAddressColumns(shortKey, dto.mapsData);
            setShortkey(shortKey);
            dataProductAddMapsData();
        }
    }
    public boolean editDataProduct(DataProductAllDTO dto, String shortKey){
        dataProductRepository.updateDataProduct(dto.metaData, shortKey);
        dataProductInsightRepository.deleteInsights(shortKey);
        dataProductInsightRepository.deleteInsightsFilter(shortKey);
        dataProductRepository.deleteAddressColumns(shortKey);
        //
        createInsightData(dto, shortKey);
        return true;
    }
    public DataProductAllDTO getDataProductAll(String shortkey){
        DataProductDTO dataProduct = dataProductRepository.getDataProduct(shortkey);
        DataProductInsightDataDTO[] insightData = dataProductInsightRepository.getInsightsData(shortkey);
        InsightFilterDTO[] insightFilter = dataProductInsightRepository.getInsightFiltersData(shortkey);
        GoogleMapsAddressDTO mapsData = dataProductRepository.getMapsData(shortkey);
        //
        return new DataProductAllDTO(dataProduct,insightData,insightFilter,mapsData);
    }

    private boolean dataContainsMapsData(Map<String, Object> dataRow){
        if(dataRow.containsKey("_mapsData"))
            return true;
        return false;
    }
}
