package com.mse.datafabric;

import com.mse.datafabric.utils.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class RestClientTests {

    @Test
    void validateRestRequest() {
        String response = RestClient.execute("https://www.google.de/?hl=de", "GET", 6000, "application/json; charset=utf-8", null, null, null);
        System.out.println(response);
        Assert.hasText("<div id=\"mngb\">",response );
    }
}
