package com.mse.datafabric.immobilien.webScraper.wgGesuchtDE;

import com.mse.datafabric.immobilien.webScraper.ScrapingPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
        searchBar.sendKeys(city);

        WebElement submitButton = getElementClickableBy(By.id(domSubmitBtnElement));
        if (submitButton == null)
            return null;
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
    public List<String> getCityItemUrls(String cityWebsiteUrl) {
        if(cityWebsiteUrl == null)
            return null;
        driver.get(cityWebsiteUrl);
        
        return getElementsAttributes(By.xpath("//div[@class='row']/div/div/div/h3/a"),"href");
    }

    @Override
    public String getItemDomContent(String itemUrl) {
        driver.get(itemUrl);
        getElementBy(By.className("panel-body"));

        return getElementAttribute(By.cssSelector("body"),"innerHTML");
    }
    @Override
    public String getItemIdForUrl(String url){
        String[] splitUrl = url.split(".");
        if (splitUrl.length <= 1)
            return null;
        return splitUrl[splitUrl.length-2];
    }
    @Override
    public String getNextPageUrl(int pageCount){
        if(pageCount == 1)
            return cityWebsiteUrl;

        driver.get(cityWebsiteUrl);
        acceptCookieBanner();

        WebElement newPageButton = getElementBy(By.xpath("//a[@class='page-link' and @href='#page-"+pageCount+"']"));
        if (newPageButton == null)
            return null;
        newPageButton.click();

        WebElement waitForPageElement = getElementClickableBy(By.xpath("//span[@class='current' and text() = '2']"));
        if (waitForPageElement == null)
            return null;

        return driver.getCurrentUrl();
    }
}
