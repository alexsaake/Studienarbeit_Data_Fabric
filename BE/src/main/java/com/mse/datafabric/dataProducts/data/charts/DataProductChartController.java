package com.mse.datafabric.dataProducts.data.charts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.dataProducts.DataProductRepository;
import com.mse.datafabric.dataProducts.data.DataProductData;
import com.mse.datafabric.dataProducts.data.insights.*;
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
public class DataProductChartController {

    private final Logger myLogger;
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    private DataProductRepository dataProductRepository;
    @Autowired
    private DataProductChartRepository dataProductChartRepository;
    @Autowired
    private DataProductData productData;
    @Autowired
    private DataProductCharts charts;

    public DataProductChartController() {
        myLogger = LoggerFactory.getLogger("DataProductChartController");
    }

    @PostMapping(
            value = "/DataProduct/{id}/Data/Charts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean createDataProductCharts(@PathVariable long id, @RequestBody String requestBodyJson){
        DataProductChartsDTO[] dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, DataProductChartsDTO[].class);
            return dataProductChartRepository.insertCharts(id, dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{id}/Data/Charts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getDataProductCharts(@PathVariable long id){
        charts.getData(id);
        //
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(charts.getChartData(id));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return "{}";
    }
    @GetMapping(
            value = "/DataProducts/Charts/Types",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getChartTypes(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(dataProductChartRepository.getChartTypes());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return jsonString;
    }
}
