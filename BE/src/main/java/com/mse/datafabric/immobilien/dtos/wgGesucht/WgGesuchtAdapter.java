package com.mse.datafabric.immobilien.dtos.wgGesucht;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mse.datafabric.immobilien.dtos.ImmobilienBean;
import com.mse.datafabric.utils.RestClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class WgGesuchtAdapter {

    public WgGesuchtResponseDto getResponseForCityId(int cityId, boolean apartments, boolean flatshares, String furnished, String locale, boolean onlyVerified, int page, String rentalType, int size, boolean studentApartments) throws JsonProcessingException {
        String response = RestClient.execute("https://api.wg-suche.de/v1_3/search/offer?apartments=" + apartments + "&cityId=" + cityId + "&flatshares=" + flatshares + "&furnished=" + furnished + "&locale=" + locale + "&onlyVerified=" + onlyVerified + "&page=" + page + "&rentalType=" + rentalType + "&size=" + size + "&studentApartments=" + studentApartments, "GET", 6000, "application/json; charset=utf-8", null, null, null);
        System.out.println(response);
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new MillisOrLocalDateTimeDeserializer());;
        objectMapper.registerModule(javaTimeModule);
        WgGesuchtResponseDto result = objectMapper.readValue(response, WgGesuchtResponseDto.class);
        return result;
    }

    public List<ImmobilienBean> getAndMapWgResponseToImmobilienBeanList() throws JsonProcessingException {
        List<ImmobilienBean> immobilienBeanList = new ArrayList<>();
        WgGesuchtResponseDto wgGesuchtResponseDto = getResponseForCityId(3663, true, true, "all", "de", false, 1, "all", 20, true);

        for (WgGesuchtDto wgGesuchtDto: wgGesuchtResponseDto.getResult()) {
            ImmobilienBean immobilienBean = new ImmobilienBean();
            immobilienBean.setPortalId(String.valueOf(wgGesuchtDto.getId()));
            immobilienBean.setTitle(wgGesuchtDto.getTitle());
            //immobilienBean.setSize(wgGesuchtDto.getSize());
            immobilienBean.setSize(wgGesuchtDto.getFlatSize() != null ? wgGesuchtDto.getFlatSize() : wgGesuchtDto.getSize()); //? wie mit inkositenten Daten umgehen?
            immobilienBean.setRent(wgGesuchtDto.getRent());
            immobilienBeanList.add(immobilienBean);
        }
        return immobilienBeanList;
    }
}
