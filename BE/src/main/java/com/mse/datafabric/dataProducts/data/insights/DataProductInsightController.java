package com.mse.datafabric.dataProducts.data.insights;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.dataProducts.DataProductRepository;
import com.mse.datafabric.dataProducts.data.DataProductData;
import com.mse.datafabric.utils.GoogleMapsAPI;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;

@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class DataProductInsightController {

    private final Logger myLogger;
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    private DataProductRepository dataProductRepository;
    @Autowired
    private DataProductInsightRepository dataProductInsightRepository;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;
    @Autowired
    private DataProductInsights insights;
    @Autowired
    private DataProductData productData;

    public DataProductInsightController() {
        myLogger = LoggerFactory.getLogger("DataProductInsightController");
    }

    @PostMapping(
            value = "/DataProduct/{id}/Data/Insights",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean createDataProductInsights(@PathVariable int id, @RequestBody String requestBodyJson){
        DataProductInsightDataDTO[] dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, DataProductInsightDataDTO[].class);
            return dataProductInsightRepository.insertInsights(id, dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }
    @PostMapping(
            value = "/DataProduct/{id}/Data/Insights/Filter",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean createDataProductInsightsFilter(@PathVariable int id, @RequestBody String requestBodyJson){
        InsightFilterDTO[] dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, InsightFilterDTO[].class);
            return dataProductInsightRepository.insertInsightsFilter(id, dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }
    @PostMapping(
            value = "/DataProduct/{id}/Data/MapsData/Filter",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean createDataProductMapsData(@PathVariable int id, @RequestBody String requestBodyJson){
        GoogleMapsAddressDTO dto;
        boolean tRet = false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, GoogleMapsAddressDTO.class);
            if(dto.city == null || dto.city.equals("")||dto.street == null || dto.street.equals(""))
                return false;
            tRet = dataProductRepository.setAddressColumns(id, dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        setDataProductMapsData(id);
        return tRet;
    }
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{id}/Data/Insights",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getDataProductInsights(@PathVariable int id, @PathParam(value="filterKeys") String filterKeys,
                                         @PathParam(value="filterValues") String filterValues){

        DataProductInsightFilter filter = new DataProductInsightFilter(filterKeys,filterValues,id,dataProductInsightRepository);
        //
        insights.getData(id, filter);
        //
        DataProductInsightsDTO insightsDTO = new DataProductInsightsDTO();
        insightsDTO.insightData = insights.getInsights(id);
        insightsDTO.insightCount = insights.getDataCount();
        insightsDTO.mapsData = insights.getInsightMapsData();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(insightsDTO);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return "{}";
    }
    @GetMapping(
            value = "/DataProducts/Insights/Types",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getInsightTypes(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(dataProductInsightRepository.getInsightTypes());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return jsonString;
    }
    @GetMapping(
            value = "/DataProducts/Insights/FilterTypes",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getInsightFilterTypes(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(dataProductInsightRepository.getFilterTypes());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return jsonString;
    }
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{id}/Data/Insights/Filter",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getInsightFilters(@PathVariable int id){
        InsightFilterDTO[] filter =  dataProductInsightRepository.getInsightFilters(id);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(filter);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return "{}";
    }
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{id}/Data/Insights/Filter/{filter_id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getInsightFilterValues(@PathVariable int id,@PathVariable int filter_id){
        String[] values =  insights.getDifferentColumnValues(id, filter_id);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(values);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return "{}";
    }
    @ShellMethod( "getDataProduct" )
    @PostMapping(
            value = "/DataProduct/{id}/Data/MapsData",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public String setDataProductMapsData(@PathVariable int id){
        productData.setId(id);
        int count = productData.dataProductAddMapsData();

        return "{\"updated items\":"+ count +"}";
    }
}
