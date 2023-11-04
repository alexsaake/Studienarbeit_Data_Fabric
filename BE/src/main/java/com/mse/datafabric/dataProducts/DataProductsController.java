package com.mse.datafabric.dataProducts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.dataProducts.data.DataProductData;
import com.mse.datafabric.dataProducts.models.*;
import com.mse.datafabric.utils.GoogleMapsAPI;
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
public class DataProductsController {
    private final Logger myLogger;
    private final IDataProductsService myDataProductsService;
    private final AuthenticationService myAuthenticationService;

    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    private DataProductRepository dataProductRepository;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;
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

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataProductId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductSummary(@PathVariable long dataProductId){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductSummary(dataProductId));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataProductId}/Details",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductDetails(@PathVariable long dataProductId){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductDetails(dataProductId));
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
    public long createDataProduct(@RequestBody String requestBodyJson){
        DataProductAllDTO dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, DataProductAllDTO.class);
            return productData.createDataProduct(dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return -1;
    }
    @PatchMapping(
            value = "/DataProduct/{dataProductId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean editDataProduct(@PathVariable long dataProductId, @RequestBody String requestBodyJson){
        DataProductAllDTO dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, DataProductAllDTO.class);
            return productData.editDataProduct(dto, dataProductId);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }
    @GetMapping(
            value = "/DataProduct/{dataProductId}/DataAll",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public String getDataProductDataAll(@PathVariable long dataProductId){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(productData.getDataProductAll(dataProductId));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
            return null;
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping(
            value = "/DataProduct/{dataProductId}"
    )
    public void deleteDataProduct(@PathVariable long dataProductId){
        myDataProductsService.softDeleteDataProduct(myAuthenticationService.getCurrentUserName(), dataProductId);
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataProductId}/Data",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductDetailData(@PathVariable long dataProductId){
        return dataProductRepository.getData(dataProductId);
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataProductId}/Ratings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductRatings(@PathVariable long dataProductId){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductRatings(dataProductId));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataProductId}/Ratings/Averages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Float getAvgRatings(@PathVariable long dataProductId){
        return dataProductRepository.getAvgRatings(dataProductId);
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/Rating/{ratingId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductRating(@PathVariable long ratingId){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductRatingDetails(ratingId));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "postDataProduct" )
    @PostMapping(
            value = "/DataProduct/{dataProductId}/Rating"
    )
    public void setDataProductRating(@PathVariable long dataProductId, @RequestBody String requestBodyJson){
        RatingDetailsDto ratingDetails = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ratingDetails = mapper.readValue(requestBodyJson, RatingDetailsDto.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        myDataProductsService.setDataProductsRating(myAuthenticationService.getCurrentUserName(), dataProductId, ratingDetails);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "putDataProduct" )
    @PutMapping(
            value = "/DataProduct/Rating/{ratingId}"
    )
    public void updateDataProductRating(@PathVariable long ratingId, @RequestBody String requestBodyJson){
        RatingDetailsDto ratingDetails = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ratingDetails = mapper.readValue(requestBodyJson, RatingDetailsDto.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        myDataProductsService.updateDataProductsRating(myAuthenticationService.getCurrentUserName(), ratingId, ratingDetails);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "deleteDataProduct" )
    @DeleteMapping(
            value = "/DataProduct/Rating/{ratingId}"
    )
    public void deleteDataProductRating(@PathVariable long ratingId){
        myDataProductsService.markAsDeletedDataProductRating(myAuthenticationService.getCurrentUserName(), ratingId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataProductId}/Rating/CanSubmit"
    )

    public String getDataProductRatingCanSubmit(@PathVariable long dataProductId){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsService.getDataProductRatingCanSubmit(myAuthenticationService.getCurrentUserName(), dataProductId));
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

