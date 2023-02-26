package com.mse.datafabric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class DataProductsController {

    private final Logger log = LoggerFactory.getLogger( getClass() );

    private final IDataProductsProvider myDataProductsProvider;
    public DataProductsController(IDataProductsProvider dataProductsProvider){
        myDataProductsProvider = dataProductsProvider;
    }

    @ShellMethod( "getDataProducts" )
    @GetMapping("/DataProducts")
    public String getDataProducts(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsProvider.getDataProducts());
        }
        catch (JsonProcessingException e) {
            log.error("Could not parse json " + e);
        }

        return jsonString;
    }
}

