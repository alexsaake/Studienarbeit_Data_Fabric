package com.mse.datafabric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Gateway")
public class DataProductsController {
    private final IDataProductsProvider myDataProductsProvider;
    public DataProductsController(IDataProductsProvider dataProductsProvider){
        myDataProductsProvider = dataProductsProvider;
    }

    @GetMapping("/DataProducts")
    public String Index(){
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(myDataProductsProvider.getDataProducts());
        }
        catch (JsonProcessingException e) {
            System.out.println("Could not parse json " + e);
        }

        return jsonString;
    }
}
