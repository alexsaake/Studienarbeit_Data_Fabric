package com.mse.datafabric;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;


@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class DataProductsController {

    private final Logger myLogger;
    private final IDataProductsProvider myDataProductsProvider;

    @Autowired
    public DataProductsController(IDataProductsProvider dataProductsProvider)
    {
        this(dataProductsProvider, LoggerFactory.getLogger("DataProductsController"));
    }

    public DataProductsController(IDataProductsProvider dataProductsProvider, Logger logger)
    {
        myDataProductsProvider = dataProductsProvider;
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
            jsonString = mapper.writeValueAsString(myDataProductsProvider.getDataProductsOverview());
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
            jsonString = mapper.writeValueAsString(myDataProductsProvider.getDataProductDetail(dataproduct_key));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return jsonString;
    }
}

