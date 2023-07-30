package com.mse.datafabric.dataProducts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.dataProducts.models.DataProductInsightsDTO;
import com.mse.datafabric.dataProducts.models.DataProductRatingDto;
import com.mse.datafabric.dataProducts.models.DataProductSQLFilterDTO;
import com.mse.datafabric.dataProducts.models.DataProductSQLWhitelists;
import com.mse.datafabric.utils.TableJsonConverter;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Base64;


@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class DataProductsController {

    private final Logger myLogger;
    private final IDataProductsService myDataProductsService;
    private final AuthenticationService myAuthenticationService;

    @Autowired
    private TableJsonConverter tableJsonConverter;
    @Autowired
    private DataProductRepository dataProductRepository;

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
            myLogger.error("Could not load Image: " + e);
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

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Data",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductDetailData(@PathVariable String dataproduct_key){
        switch (dataproduct_key){
            case "immobilien":
                try {
                    return tableJsonConverter.getAllTableDataAsJsonString("IMMO_DATA");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        }
        return "{}";
    }
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Data/Insights",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getDataProductInsights(@PathVariable String dataproduct_key, @PathParam(value="areaFilter") String areaFilter, @PathParam(value="dateFromFilter") String dateFromFilter, @PathParam(value="dateToFilter") String dateToFilter){
        DataProductSQLFilterDTO filterValues = new DataProductSQLFilterDTO(areaFilter,dateFromFilter,dateToFilter);
        switch (dataproduct_key){
            case "immobilien":
                DataProductInsightsDTO insightsDTO = new DataProductInsightsDTO();
                insightsDTO.averageRent = dataProductRepository.getInsightAverage(DataProductSQLWhitelists.IMMO_RENT, filterValues);
                insightsDTO.averageSize = dataProductRepository.getInsightAverage(DataProductSQLWhitelists.IMMO_SIZE, filterValues);
                insightsDTO.activeItemsCount = dataProductRepository.getInsightCount(DataProductSQLWhitelists.IMMO_COUNT, filterValues);
                insightsDTO.highestRent = dataProductRepository.getInsightHighest(DataProductSQLWhitelists.IMMO_RENT, filterValues);
                insightsDTO.lowestRent = dataProductRepository.getInsightLowest(DataProductSQLWhitelists.IMMO_RENT, filterValues);
                insightsDTO.medianRent = dataProductRepository.getInsightMedian(DataProductSQLWhitelists.IMMO_RENT, filterValues);
                insightsDTO.quartile25Rent = dataProductRepository.getInsightQuartile25(DataProductSQLWhitelists.IMMO_RENT, filterValues);
                insightsDTO.quartile75Rent = dataProductRepository.getInsightQuartile75(DataProductSQLWhitelists.IMMO_RENT, filterValues);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.writeValueAsString(insightsDTO);
                }
                catch (JsonProcessingException e) {
                    myLogger.error("Could not parse json " + e);
                }
        }
        return "{}";
    }
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataproduct_key}/Data/Cities",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public String getDataProductColumnValues(@PathVariable String dataproduct_key){
        switch (dataproduct_key){
            case "immobilien":
                String[] cityValues =  dataProductRepository.getDifferentColumnValues(DataProductSQLWhitelists.IMMO_CITY);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.writeValueAsString(cityValues);
                }
                catch (JsonProcessingException e) {
                    myLogger.error("Could not parse json " + e);
                }
        }
        return "{}";
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

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "getDataProduct" )
    @PostMapping(
            value = "/DataProduct/{dataproduct_key}/Rating"
    )
    public void setDataProductRatings(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
        if(!myDataProductsService.getDataProductRatingCanSubmit(dataproduct_key, myAuthenticationService.getCurrentUserName())){
            return;
        }
        DataProductRatingDto dataProductRating = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            dataProductRating = mapper.readValue(requestBodyJson, DataProductRatingDto.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        dataProductRating.setShortKey(dataproduct_key);
        dataProductRating.setUserName(myAuthenticationService.getCurrentUserName());

        myDataProductsService.setDataProductsRating(dataProductRating);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "getDataProduct" )
    @PutMapping(
            value = "/DataProduct/{dataproduct_key}/Rating"
    )
    public void updateDataProductRatings(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
        if(myDataProductsService.getDataProductRatingCanSubmit(dataproduct_key, myAuthenticationService.getCurrentUserName())){
            return;
        }
        DataProductRatingDto dataProductRating = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            dataProductRating = mapper.readValue(requestBodyJson, DataProductRatingDto.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        dataProductRating.setShortKey(dataproduct_key);
        dataProductRating.setUserName(myAuthenticationService.getCurrentUserName());

        myDataProductsService.updateDataProductsRating(dataProductRating);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "getDataProduct" )
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
            value = "/DataProduct/Rating/Comment/MaxLength",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductRatingCommentMaxLength(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductRatingCommentMaxLength());
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }
}

