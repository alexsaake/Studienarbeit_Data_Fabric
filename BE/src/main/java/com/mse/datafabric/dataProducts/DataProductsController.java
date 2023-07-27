package com.mse.datafabric.dataProducts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.dataProducts.models.DataProductRatingDto;
import com.mse.datafabric.utils.TableJsonConverter;
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
            value = "/DataProduct/{dataproduct_key}/Rating",
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
    @PutMapping(
            value = "/DataProduct/{dataproduct_key}/Rating"
    )
    public void setDataProductRatings(@PathVariable String dataproduct_key, @RequestBody String requestBodyJson){
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

