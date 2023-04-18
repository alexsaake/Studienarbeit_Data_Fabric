package com.mse.datafabric.immobilien.dtos.wgGesucht;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mse.datafabric.utils.RestClient;

import java.time.LocalDateTime;

public class TestRestClient {
    public static void main(String[] args) throws JsonProcessingException {
        WgGesuchtResponseDto result = getResponseForCityId(3663, true, true, "all", "de", false, 1, "all", 20, true);
        System.out.println(result.getRefreshDate());
        for (WgGesuchtDto wgGesuchtItem: result.getResult()
             ) {
            System.out.println(wgGesuchtItem.getTitle());
            System.out.println(wgGesuchtItem.getFlatSize());
            System.out.println(wgGesuchtItem.getSize());
            System.out.println(wgGesuchtItem.getRent());
            System.out.println(wgGesuchtItem.getRent());
            if (wgGesuchtItem.getSize() != null) {
                System.out.println("Qm2Preise: " + wgGesuchtItem.getRent()/wgGesuchtItem.getSize());
            } else {
                System.out.println("Qm2Preise: " + wgGesuchtItem.getRent()/wgGesuchtItem.getFlatSize());
            }

            System.out.println("---------------------");
        }
        System.out.println(result.toString());
    }

    private static WgGesuchtResponseDto getResponseForCityId(int cityId, boolean apartments, boolean flatshares, String furnished, String locale, boolean onlyVerified, int page, String rentalType, int size, boolean studentApartments) throws JsonProcessingException {
        String response = RestClient.execute("https://api.wg-suche.de/v1_3/search/offer?apartments=" + apartments + "&cityId=" + cityId + "&flatshares=" + flatshares + "&furnished=" + furnished + "&locale=" + locale + "&onlyVerified=" + onlyVerified + "&page=" + page + "&rentalType=" + rentalType + "&size=" + size + "&studentApartments=" + studentApartments, "GET", 6000, "application/json; charset=utf-8", null, null, null);
        System.out.println(response);
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new MillisOrLocalDateTimeDeserializer());;
        objectMapper.registerModule(javaTimeModule);
        WgGesuchtResponseDto result = objectMapper.readValue(response, WgGesuchtResponseDto.class);
        return result;
    }
}
