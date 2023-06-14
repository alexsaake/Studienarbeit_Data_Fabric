package com.mse.datafabric.immobilien.webScraper.wgGesuchtDE;

import com.mse.datafabric.immobilien.webScraper.ScrapingDom;
import com.mse.datafabric.immobilien.webScraper.ScrapingPage;
import com.mse.datafabric.immobilien.webScraper.dtos.CityItemDTO;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


public class PageWgGesuchtDe extends ScrapingPage {

    public PageWgGesuchtDe(String startWebsiteUrl, String domQueryInputElement, String domSubmitFormElement, String domSubmitBtnElement, String pageGetParam) {
        super(startWebsiteUrl, domQueryInputElement, domSubmitFormElement, domSubmitBtnElement, pageGetParam);

    }
    public void acceptCookieBanner(){
        WebElement cookieBar = getElementBy(By.xpath("//a[contains(@class, 'cmptxt_btn_yes')]"));
        if (cookieBar == null)
            return;
        cookieBar.click();
    }

    @Override
    public String getCityWebsiteUrl(String city) {
        driver.get(startWebsiteUrl);

        acceptCookieBanner();

        WebElement searchBar = getElementBy(By.id(domQueryInputElement));
        if (searchBar == null)
            return null;
        sendKeysAsHuman(searchBar,city);

        WebElement submitButton = getElementClickableBy(By.id(domSubmitBtnElement));
        if (submitButton == null)
            return null;
        submitButton.click();

        submitButton = getElementClickableBy(By.id(domSubmitBtnElement));
        if (submitButton != null)
            submitButton.click();

        WebElement waitForPageElement = getElementClickableBy(By.xpath("//div[contains(@class, 'offer_list_item')]"));
        if (waitForPageElement == null)
            return null;

        return driver.getCurrentUrl();
    }
    @Override
    public void afterBrowserInit(){

    }

    @Override
    public List<CityItemDTO> getCityItemUrls(String cityWebsiteUrl) {
        if(cityWebsiteUrl == null)
            return null;
        driver.get(cityWebsiteUrl);

        List<WebElement> elements = getElementsBy(By.xpath("//div[@class='row']/div/div/div/h3/a"));
        List<CityItemDTO> dtos = new ArrayList<>();
        elements.forEach(element-> {
            CityItemDTO dto = new CityItemDTO();
            dto.url = element.getAttribute("href");
            dto.itemCardContent = element.findElement(By.xpath("./../../../..")).getAttribute("innerHTML");;
            dtos.add(dto);
        });
        return dtos;
    }

    @Override
    public String getItemDomContent(String itemUrl) {
        driver.get(itemUrl);
        getElementBy(By.className("panel-body"));

        return getElementAttribute(By.cssSelector("head"),"innerHTML") + getElementAttribute(By.cssSelector("body"),"innerHTML");
    }
    @Override
    public String getItemIdForUrl(String url){
        String[] splitUrl = url.split("\\.");
        if (splitUrl.length <= 1)
            return null;
        return splitUrl[splitUrl.length-2];
    }
    @Override
    public String getNextPageUrl(int pageCount){
        if(pageCount == 1) {
            return cityWebsiteUrl;
        }

        driver.get(cityWebsiteUrl);
        acceptCookieBanner();

        WebElement newPageButton = getElementBy(By.xpath("//span[@class='ellipse clickable' and text() ='…']"));
        if (newPageButton == null)
            return null;
        newPageButton.click();

        WebElement newPageButtonInput = getElementBy(By.xpath("//span[@class='ellipse clickable']/input"));
        if (newPageButtonInput == null)
            return null;

        newPageButtonInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        newPageButtonInput.sendKeys(String.valueOf(pageCount));
        newPageButtonInput.sendKeys(Keys.ENTER);

        WebElement waitForPageElement = getElementClickableBy(By.xpath("//span[@class='current' and text() = '"+pageCount+"']"));
        if (waitForPageElement == null)
            return null;

        return driver.getCurrentUrl();
    }
    @Override
    public ScrapingDom initScrapingDom(CityItemDTO dto){
        return new DomWgGesuchtDe(dto);
    }
}
