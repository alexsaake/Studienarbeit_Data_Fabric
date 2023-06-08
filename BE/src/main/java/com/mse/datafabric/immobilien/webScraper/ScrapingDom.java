package com.mse.datafabric.immobilien.webScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public abstract class ScrapingDom {
    public static final Logger LOGGER= LoggerFactory.getLogger(ScrapingDom.class);
    public Document domContentDocument;

    public String itemId;
    public String city;
    public int index;
    public ScrapingDom(String domContent, int index, String itemId, String city){
        this.index = index;
        this.itemId = itemId;
        this.city = city;
        parseStringToTHML(domContent);
    }

    public ScrapingContentDTO getContentToDTO(){
        ScrapingContentDTO dto = new ScrapingContentDTO();
        dto.index = this.index;
        dto.portalId = getPortalId();
        dto.itemId = itemId;
        dto.flatSize = getFlatSize();
        dto.rent = getRent();
        dto.roomSize = getRoomSize();
        dto.deposit = getDeposit();
        dto.extraCharges = getExtraCharges();
        dto.from = getFrom();
        dto.cityName = getCityName();
        dto.borough = getBorough();
        dto.furnished = getFurnished();
        dto.status = getStatus();
        dto.creationDate = getCreationDate();
        dto.title = getTitle();
        dto.dtoSaved = false;

        if (dto.title == null)
            LOGGER.info("Empty DTO created!");
        else
            LOGGER.info("DTO successfully created!");

        return dto;
    }

    public void parseStringToTHML(String content){
        String html = content;
        domContentDocument = Jsoup.parse(html);
    }

    public String getCityName(){
        return city;
    }

    public abstract String getPortalId();
    public abstract String getFlatSize();
    public abstract String getRent();
    public abstract String getRoomSize();
    public abstract String getTitle();
    public abstract String getExtraCharges();
    public abstract String getFrom();
    public abstract String getBorough();
    public abstract String getFurnished();
    public abstract String getStatus();
    public abstract String getCreationDate();
    public abstract String getDeposit();


}
