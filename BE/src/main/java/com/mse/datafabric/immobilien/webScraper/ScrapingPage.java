package com.mse.datafabric.immobilien.webScraper;


import com.mse.datafabric.immobilien.webScraper.dtos.CityItemDTO;
import com.mse.datafabric.immobilien.webScraper.dtos.ScrapingContentDTO;
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
import java.util.Locale;
import java.util.Random;


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
    public List<CityItemDTO> cityItems;

    private String status;

    private int activePageCount;
    private int activeItemCount;

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
        status = "running";
        activeItemCount = 0;
        //
        initBrowserFirefox();
    }

    public void quitBrowser(){
        status = "stopped";
        driver.quit();
    }
    public void scrapeAllPages(String cityName){
        try {
            activePageCount = 1;

            cityWebsiteUrl = getCityWebsiteUrl(cityName);
            if (cityWebsiteUrl == null) {
                status = "error";
            }
            if(status.equals("error"))
                return;
            while ((cityItems = getCityItemUrls(getNextPageUrl(activePageCount)))!=null && cityItems.size()>0)
            {
                if(status.equals("stopping") || status.equals("error")){
                    quickSaveDTO();
                    return;
                }
                if(!initBrowserFirefox()){
                    status = "error";
                    return;
                };
                afterBrowserInit();
                scrapeItems(cityName);
                if(!quickSaveDTO())
                    return;
                activePageCount++;
            }
            status = "finished";
        }catch(Exception e){
            status = "error";
        }
    }
    public String getStatus(){
        return status;
    }
    public int getPageCount(){
        return activePageCount;
    }
    public int getItemCount(){
        return activeItemCount;
    }
    public List<ScrapingContentDTO> getItems(){
        return cityItemDTOs;
    }
    public void stopScraper(){
        if(!status.equals("running"))
            return;
        status = "stopping";
    }
    public void scrapeItems(String cityName){

        cityItems.forEach(item-> {
            if (status.equals("stopping") || status.equals("error"))
                return;
            //
            if (item.url == null || !item.url.contains(startWebsiteUrl))
                return;
            item.itemContent = getItemDomContent(item.url);
            item.itemId = getItemIdForUrl(item.url);
            item.index = cityItemDTOs.size();
            item.city = cityName;
            if (item.itemContent == null)
                return;
            ScrapingDom domContent = initScrapingDom(item);
            cityItemDTOs.add(domContent.getContentToDTO());
            waitRandomTime(8, 12);
            //
            activeItemCount++;
        });

    }
    public void waitRandomTime(int fromTimeInSec, int toTimeInSec){
        Random rand = new Random();
        long value = rand.nextInt((toTimeInSec - fromTimeInSec) + 1) + fromTimeInSec;
        try {
            Thread.sleep(value * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean quickSaveDTO(){
        ScraperRepository repo = new ScraperRepository();
        return repo.saveDTOToDatabase(cityItemDTOs);
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
                return element.getAttribute(attribute);
            } catch (StaleElementReferenceException e) {
                element = getElementBy(elementBy);
                currentAttempts++;
            }
        }
        return null;
    }
    public void sendKeysAsHuman(WebElement we, String key){
        String[] keys = key.split("");
        for (int i = 0;i<keys.length;i++){
            we.sendKeys(keys[i]);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

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
        String applicationName;
        switch (getOperatingSystemType()){
            case "mac":
                return null;
            case "windows":
                applicationName = "geckodriver.exe";
                break;
            case "linux":
                return null;
            default:
                return null;
        }
        Resource resource = new ClassPathResource("SeleniumDriver/"+ applicationName);
        try {
            return new File(resource.getURI()).getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String getOperatingSystemType() {
        String detectedOS;
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            detectedOS = "mac";
        } else if (OS.contains("win")) {
            detectedOS = "windows";
        } else if (OS.contains("nux")) {
            detectedOS = "linux";
        } else {
            detectedOS = "other";
        }
        return detectedOS;
    }

    private boolean initBrowserFirefox(){
        String driverPtah = getDriverPath();
        if (driverPtah == null){
            System.console().printf("No selenium driver available on this OS!");
            return false;
        }
        try {
            if (driver != null) {
                driver.quit();
            }
            System.setProperty("webdriver.gecko.driver", getDriverPath());

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
            profile.setPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) PC" + System.currentTimeMillis() + " Firefox/112.0");


            FirefoxOptions capabilities = new FirefoxOptions();
            capabilities.addArguments("-private");
            capabilities.setProfile(profile);

            driver = new FirefoxDriver(capabilities);

            Dimension dimension = new Dimension(1100, 600);
            driver.manage().window().setSize(dimension);

            ((JavascriptExecutor) driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            driverWaiter = new WebDriverWait(driver, Duration.ofSeconds(DRIVER_TIMEOUT));
        }catch(Exception e){
            status = "error";
            return false;
        }
        return true;
    }
    public abstract void afterBrowserInit();
    public abstract String getCityWebsiteUrl(String city);
    public abstract List<CityItemDTO> getCityItemUrls(String cityWebsiteUrl);
    public abstract String getItemDomContent(String itemUrl);
    public abstract String getItemIdForUrl(String url);
    public abstract String getNextPageUrl(int pageCount);

    public abstract ScrapingDom initScrapingDom(CityItemDTO dto);
}
