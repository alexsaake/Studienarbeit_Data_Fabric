package com.mse.datafabric.immobilien.dtos.wgSuche;

import com.mse.datafabric.utils.RestClient;

public class TestWgSuche {

    public static void main(String[] args) {
        String response = RestClient.execute("https://www.wg-suche.de/angebot/101868052/dg-wohnung-in-nbg-gostenhof");
        System.out.println(response);
    }
}
