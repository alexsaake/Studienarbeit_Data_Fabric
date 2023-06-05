package com.mse.datafabric.immobilien.webScraper.wgSucheDe;

import com.mse.datafabric.immobilien.webScraper.ScrapingDom;
import com.mse.datafabric.immobilien.webScraper.wgGesuchtDE.DomWgGesuchtDe;
import org.openqa.selenium.WebElement;
import com.mse.datafabric.immobilien.webScraper.ScrapingPage;
import org.openqa.selenium.By;
import java.util.List;


public class PageWgSucheDe extends ScrapingPage {

    public PageWgSucheDe(String startWebsiteUrl, String domQueryInputElement, String domSubmitFormElement, String domSubmitBtnElement, String pageGetParam) {
        super(startWebsiteUrl, domQueryInputElement, domSubmitFormElement, domSubmitBtnElement, pageGetParam);

    }

    @Override
    public String getCityWebsiteUrl(String city) {
        driver.get(startWebsiteUrl);

        WebElement searchBar;
        searchBar = getElementBy(By.name(domQueryInputElement));
        if (searchBar == null)
            return null;
        searchBar.sendKeys(city);

        return getElementAttribute(By.xpath("//a[contains(@class, 'dropdown-menu__result')]"),"href");
    }
    @Override
    public void afterBrowserInit(){

    }
    @Override
    public List<String> getCityItemUrls(String cityWebsiteUrl) {
        driver.get(cityWebsiteUrl);
        
        return getElementsAttributes(By.xpath("//wgs-search-offer-list-item/div/a"),"href");
    }

    @Override
    public String getItemDomContent(String itemUrl) {
        driver.get(itemUrl);
        getElementBy(By.className("odp-dataset"));

        return getElementAttribute(By.cssSelector("body"),"innerHTML");
    }
    @Override
    public String getItemIdForUrl(String url){
        String[] splitUrl = url.split("/");
        if (splitUrl.length <= 1)
            return null;
        return splitUrl[splitUrl.length-2];
    }
    @Override
    public String getNextPageUrl(int pageCount){
        return cityWebsiteUrl+"?"+pageGetParam+"="+pageCount;
    }
    @Override
    public ScrapingDom initScrapingDom(String itemContent, String itemId, String cityName){
        return new DomWgSucheDe(itemContent, itemId, cityName);
    }
}
