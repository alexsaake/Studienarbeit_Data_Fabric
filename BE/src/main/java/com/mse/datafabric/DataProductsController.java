package com.mse.datafabric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


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

    @ShellMethod( "getDataProducts/image" )
    @GetMapping(
            value = "/DataProducts/image/{dataproduct_key}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getDataProductImage(@PathVariable String dataproduct_key) throws IOException {
        byte[]  file = null;
        try {
            file = (new ClassPathResource(dataproduct_key+".jpg")).getInputStream().readAllBytes();
        }
        catch (Exception e) {
            log.error("Could not load Image: " + e);
        }
        return file;
    }
}

