package com.mse.datafabric.immobilien.webScraper.immoscout24De;

import com.mse.datafabric.immobilien.webScraper.ScrapingDom;
import com.mse.datafabric.immobilien.webScraper.ScrapingPage;
import com.mse.datafabric.immobilien.webScraper.wgSucheDe.DomWgSucheDe;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;


public class PageImmoscout24De extends ScrapingPage {

    public PageImmoscout24De(String startWebsiteUrl, String domQueryInputElement, String domSubmitFormElement, String domSubmitBtnElement, String pageGetParam) {
        super(startWebsiteUrl, domQueryInputElement, domSubmitFormElement, domSubmitBtnElement, pageGetParam);

    }

    @Override
    public String getCityWebsiteUrl(String city) {
        driver.get(startWebsiteUrl);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String script = "return document.getElementById('usercentrics-root').shadowRoot.querySelector('button[data-testid=uc-accept-all-button]')";
        WebElement cookieBanner = (WebElement)((JavascriptExecutor)driver).executeScript(script);
        cookieBanner.click();

        WebElement searchBar = getElementBy(By.id(domQueryInputElement));
        if (searchBar == null)
            return null;
        searchBar.sendKeys(city);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement searchButton = getElementBy(By.xpath("//form[@id='"+domSubmitFormElement+"']/article/div/div/button"));
        if (searchButton == null)
            return null;
        searchButton.click();
        searchButton.click();

        do{
            WebElement captcha = getElementBy(By.xpath("//*[@class='geetest_radar_tip']"));
            if (searchButton == null)
                return null;
            captcha.click();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }while(getElementBy(By.xpath("//*[@class='geetest_radar_tip']"))!=null);


        return driver.getCurrentUrl();
    }

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
        return new DomImmoscout24De(itemContent, itemId, cityName);
    }
}
