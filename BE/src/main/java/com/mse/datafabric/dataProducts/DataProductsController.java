package com.mse.datafabric.dataProducts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.dataProducts.data.DataProductData;
import com.mse.datafabric.dataProducts.data.insights.*;
import com.mse.datafabric.dataProducts.models.*;
import com.mse.datafabric.utils.GoogleMapsAPI;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;


@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class DataProductsController {
    private final Logger myLogger;
    private final IDataProductsService myDataProductsService;
    private final AuthenticationService myAuthenticationService;

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

    @Autowired
    public DataProductsController(IDataProductsService dataProductsProvider, AuthenticationService authenticationService)
    {
        this(dataProductsProvider, authenticationService, LoggerFactory.getLogger("DataProductsController"));
    }
    public DataProductsController(IDataProductsService dataProductsProvider, AuthenticationService authenticationService, Logger logger)
    {
        myDataProductsService = dataProductsProvider;
        myAuthenticationService = authenticationService;
        myLogger = logger;
    }

    @ShellMethod( "getDataProducts" )
    @GetMapping(
            value = "/DataProducts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductsOverview(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductsOverview());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }

    @GetMapping(
            value = "/DataProducts/Categories",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductsCategories(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(dataProductRepository.getCategories());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return jsonString;
    }
    @GetMapping(
            value = "/DataProducts/AccessRights",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductsAccessRights(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(dataProductRepository.getAccessRights());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return jsonString;
    }

    @ShellMethod( "getDataProduct/Image" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Image",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public @ResponseBody byte[] getDataProductImage(@PathVariable String dataproduct_key) {
        byte[]  file = null;
        try {
            file = Base64.getEncoder().encode((new ClassPathResource(dataproduct_key+".jpg")).getInputStream().readAllBytes());
        }
        catch (Exception e) {
            try {
                file = Base64.getEncoder().encode((new ClassPathResource("defaultImage.jpg")).getInputStream().readAllBytes());
            }
            catch (Exception e2) {
                myLogger.error("Could not load Image: " + e2);
            }
        }
        return file;
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductDetail(@PathVariable String dataproduct_key){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductDetail(dataproduct_key));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }
    @PostMapping(
            value = "/DataProduct",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public String createDataProduct(@RequestBody String requestBodyJson){
        DataProductDTO dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            dto = mapper.readValue(requestBodyJson, DataProductDTO.class);
            return dataProductRepository.insertDataProduct(dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return null;
    }
    @PostMapping(
            value = "/DataProduct/{dataproduct_key}/Data/Insights",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean createDataProductInsights(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
        DataProductInsightDataDTO[] dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, DataProductInsightDataDTO[].class);
            return dataProductInsightRepository.insertInsights(dataproduct_key, dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }
    @PostMapping(
            value = "/DataProduct/{dataproduct_key}/Data/Insights/Filter",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean createDataProductInsightsFilter(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
        InsightFilterDTO[] dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, InsightFilterDTO[].class);
            return dataProductInsightRepository.insertInsightsFilter(dataproduct_key, dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }

    @DeleteMapping(
            value = "/DataProduct/{dataproduct_key}"
    )
    @PreAuthorize("hasAuthority('USER')")
    public void deleteDataProduct(@PathVariable String dataproduct_key){
        myDataProductsService.softDeleteDataProduct(dataproduct_key, myAuthenticationService.getCurrentUserName());
    }

    @PostMapping(
            value = "/DataProduct/{dataproduct_key}/Data/Insights",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean createDataProductInsights(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
        DataProductInsightDataDTO[] dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, DataProductInsightDataDTO[].class);
            return dataProductInsightRepository.insertInsights(dataproduct_key, dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }
    @PostMapping(
            value = "/DataProduct/{dataproduct_key}/Data/Insights/Filter",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean createDataProductInsightsFilter(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
        InsightFilterDTO[] dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, InsightFilterDTO[].class);
            return dataProductInsightRepository.insertInsightsFilter(dataproduct_key, dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Data",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductDetailData(@PathVariable String dataproduct_key){
        return dataProductRepository.getData(dataproduct_key);

    }
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Data/Insights",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getDataProductInsights(@PathVariable String dataproduct_key, @PathParam(value="filterKeys") String filterKeys,
                                         @PathParam(value="filterValues") String filterValues){

        DataProductInsightFilter filter = new DataProductInsightFilter(filterKeys,filterValues,dataproduct_key, dataProductInsightRepository);
        //
        insights.getData(dataproduct_key, filter);
        //
        DataProductInsightsDTO insightsDTO = new DataProductInsightsDTO();
        insightsDTO.insightData = insights.getInsights(dataproduct_key);
        insightsDTO.insightCount = insights.getDataCount();
        insightsDTO.mapsData = insights.getInsightMapsData(dataproduct_key, filter);
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
            value = "/DataProduct/{dataproduct_key}/Data/Insights/Filter",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getInsightFilters(@PathVariable String dataproduct_key){
        InsightFilterDTO[] filter =  dataProductInsightRepository.getInsightFilters(dataproduct_key);
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
            value = "/DataProduct/{dataproduct_key}/Data/Insights/Filter/{filter_id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getInsightFilterValues(@PathVariable String dataproduct_key,@PathVariable int filter_id){
        String[] values =  insights.getDifferentColumnValues(dataproduct_key, filter_id);
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
            value = "/DataProduct/{dataproduct_key}/Data/MapsData",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public String setDataProductMapsData(@PathVariable String dataproduct_key){
        productData.setShortkey(dataproduct_key);
        int count = productData.dataProductAddMapsData();

        return "{\"updated items\":"+ count +"}";
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Ratings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductRatings(@PathVariable String dataproduct_key){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductRatings(dataproduct_key));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Ratings/Averages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Float getAvgRatings(@PathVariable String dataproduct_key){
        return dataProductRepository.getAvgRatings(dataproduct_key);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "postDataProduct" )
    @PostMapping(
            value = "/DataProduct/{dataproduct_key}/Rating"
    )
    public void setDataProductRatings(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
        if(!myDataProductsService.getDataProductRatingCanSubmit(dataproduct_key, myAuthenticationService.getCurrentUserName())){
            return;
        }
        RatingDto dataProductRating = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            dataProductRating = mapper.readValue(requestBodyJson, RatingDto.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        dataProductRating.setShortKey(dataproduct_key);
        dataProductRating.setUserName(myAuthenticationService.getCurrentUserName());

        myDataProductsService.setDataProductsRating(dataProductRating);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "putDataProduct" )
    @PutMapping(
            value = "/DataProduct/{dataproduct_key}/Rating"
    )
    public void updateDataProductRatings(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
        if(myDataProductsService.getDataProductRatingCanSubmit(dataproduct_key, myAuthenticationService.getCurrentUserName())){
            return;
        }
        RatingDto dataProductRating = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            dataProductRating = mapper.readValue(requestBodyJson, RatingDto.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        dataProductRating.setShortKey(dataproduct_key);
        dataProductRating.setUserName(myAuthenticationService.getCurrentUserName());

        myDataProductsService.updateDataProductsRating(dataProductRating);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "deleteDataProduct" )
    @DeleteMapping(
            value = "/DataProduct/{dataproduct_key}/Rating"
    )
    public void deleteDataProductRating(@PathVariable String dataproduct_key){
        if(myDataProductsService.getDataProductRatingCanSubmit(dataproduct_key, myAuthenticationService.getCurrentUserName())){
            return;
        }
        myDataProductsService.markAsDeletedDataProductRating(dataproduct_key, myAuthenticationService.getCurrentUserName());
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Rating/CanSubmit"
    )

    public String getDataProductRatingCanSubmit(@PathVariable String dataproduct_key){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductRatingCanSubmit(dataproduct_key, myAuthenticationService.getCurrentUserName()));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/Rating/MaxLengths",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductRatingCommentMaxLength(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductRatingMaxLengths());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }
}

