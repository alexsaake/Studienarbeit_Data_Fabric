package com.mse.datafabric.immobilien.webScraper;


import com.mse.datafabric.immobilien.webScraper.wgSucheDe.DomWgSucheDe;
import org.openqa.selenium.*;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public abstract class ScrapingPage {

    public final static int DRIVER_TIMEOUT = 10;
    public WebDriver driver;
    public WebDriverWait driverWaiter;
    public String startWebsiteUrl;
    public String domQueryInputElement;
    public String domSubmitFormElement;
    public String domSubmitBtnElement;
    public String pageGetParam;
    public String cityWebsiteUrl;
    public List<String> cityItemsUrls;

    List<ScrapingContentDTO> cityItemDTOs;


    public ScrapingPage(String startWebsiteUrl, String domQueryInputElement,
                        String domSubmitFormElement, String domSubmitBtnElement,
                        String pageGetParam) {
        this.startWebsiteUrl = startWebsiteUrl;
        this.domQueryInputElement = domQueryInputElement;
        this.domSubmitFormElement = domSubmitFormElement;
        this.domSubmitBtnElement = domSubmitBtnElement;
        this.pageGetParam = pageGetParam;
        //
        cityItemDTOs = new ArrayList<>();
        //
        initBrowserFirefox();
    }

    public void quitBrowser(){
        driver.quit();
    }
    public void scrapeAllPages(String cityName){
        int pageCount = 1;

        cityWebsiteUrl = getCityWebsiteUrl(cityName);
        if (cityWebsiteUrl == null)
            return;
        while ((cityItemsUrls = getCityItemUrls(getNextPageUrl(pageCount)))!=null && cityItemsUrls.size()>0)
        {
            initBrowserFirefox();
            afterBrowserInit();
            scrapeItems(cityName);
            quickSaveDTO();
            pageCount++;
        }
    }
    public void scrapeItems(String cityName){
        cityItemsUrls.forEach(itemUrl -> {
            String itemContent = getItemDomContent(itemUrl);
            String itemId = getItemIdForUrl(itemUrl);
            if (itemContent == null)
                return;
            DomWgSucheDe domContent = new DomWgSucheDe(itemContent, itemId, cityName);
            cityItemDTOs.add(domContent.getContentToDTO());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void quickSaveDTO(){
        ScraperRepository repo = new ScraperRepository();
        repo.saveDTOToDatabase(cityItemDTOs);

        cityItemDTOs = new ArrayList<>();
    }
    public WebElement getElementBy(By byElement){
        try {
            List<WebElement> elements = driverWaiter.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(byElement));
            if(elements.size()==0)
                return null;
            return elements.get(0);

        } catch (Exception e) {

        }
        return null;
    }
    public WebElement getElementClickableBy(By byElement){
        try {
            WebElement element = driverWaiter.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(byElement));
            return element;
        } catch (Exception e) {

        }
        return null;
    }
    public List<WebElement> getElementsBy(By byElement){
        try {
            List<WebElement> elements = driverWaiter.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(byElement));
            return elements;
        } catch (Exception e) {

        }
        return null;
    }
    public String getElementAttribute(By elementBy, String attribute){
        int currentAttempts = 0;
        WebElement element = getElementBy(elementBy);
        while (currentAttempts <= 5)
        {
            try {
                String value = element.getAttribute(attribute);
                return value;
            } catch (StaleElementReferenceException e) {
                element = getElementBy(elementBy);
                currentAttempts++;
            }
        }
        return null;
    }

    public List<String> getElementsAttributes(By elementsBy, String attribute){
        List<String> itemUrls = new ArrayList<>();
        int currentAttempts = 0;

        List<WebElement> offerItems = getElementsBy(elementsBy);
        if (offerItems == null)
            return null;

        while (currentAttempts <= 5)
        {
            try {
                offerItems.forEach(item -> {
                    itemUrls.add(item.getAttribute("href"));
                });
                return itemUrls;
            } catch (StaleElementReferenceException e) {
                offerItems = getElementsBy(elementsBy);
                currentAttempts++;
            }
        }
        return null;
    }
    private String getDriverPath(){
        Resource resource = System.getProperty("os.name").contains("Windows") ? new ClassPathResource("geckodriver.exe") : new ClassPathResource("geckodriver");

        try {
            return new File(resource.getURI()).getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void initBrowserFirefox(){
        if (driver != null){
            driver.quit();
        }
        System.setProperty("webdriver.gecko.driver",getDriverPath());

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 1);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.manager.focusWhenStarting", false);
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.closeWhenDone", true);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("dom.ipc.plugins.enabled.libflashplayer.so", false);
        profile.setPreference("disable_encoding", true);
        profile.setPreference("Network.http.accept-encoding", "");
        //profile.setPreference("network.cookie.cookieBehavior", 2);
        profile.setPreference("mitm_http2", "");
        profile.setPreference("permissions.default.image", 2);
        profile.setPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) PC"+System.currentTimeMillis() +" Firefox/112.0");

        FirefoxOptions capabilities = new FirefoxOptions();
        capabilities.setProfile(profile);

        driver = new FirefoxDriver(capabilities);

        Dimension dimension = new Dimension(1100, 600);
        driver.manage().window().setSize(dimension);

        ((JavascriptExecutor)driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

        driverWaiter = new WebDriverWait(driver, Duration.ofSeconds(DRIVER_TIMEOUT));
    }
    public abstract void afterBrowserInit();
    public abstract String getCityWebsiteUrl(String city);
    public abstract List<String> getCityItemUrls(String cityWebsiteUrl);
    public abstract String getItemDomContent(String itemUrl);
    public abstract String getItemIdForUrl(String url);
    public abstract String getNextPageUrl(int pageCount);
}
