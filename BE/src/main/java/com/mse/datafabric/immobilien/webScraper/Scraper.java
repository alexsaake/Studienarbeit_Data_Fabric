package com.mse.datafabric.immobilien.webScraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.immobilien.webScraper.wgGesuchtDE.PageWgGesuchtDe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@ShellComponent
@Component
public class Scraper extends Thread{
    public static final Logger LOGGER= LoggerFactory.getLogger(ScrapingDom.class);
    private Thread thread;
    private ScrapingPage activeScraper;
    private String activeCity;

    public String start (String city) {
        activeCity = city;
        if (thread == null) {
            thread = new Thread (this);
            thread.start();
            return "started";
        }
        return "Scraper already running... Couldnt start scraper!";
    }
    public void run(){
        runScraper(activeCity);
    }
    private void killScraper(){
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
    @ShellMethod( "runScraper" )
    public void runScraper() {
        wgGesuchtScrapeCity(Arrays.asList("Nürnberg"));
    }
    public void runScraper(String city) {
        wgGesuchtScrapeCity(Arrays.asList(city));
    }
    public void stopScraper(){
        if (activeScraper == null)
            return;
        activeScraper.stopScraper();
    }
    public String getStatus(){
        if (activeScraper == null)
            return null;
        return activeScraper.getStatus();
    }
    public int getPageCount(){
        if (activeScraper == null)
            return -1;
        return activeScraper.getPageCount() - 1;
    }

    public int getItemCount(){
        if (activeScraper == null)
            return -1;
        return activeScraper.getItemCount();
    }

    public String getItems(){
        if (activeScraper == null)
            return "{}";
        ObjectMapper Obj = new ObjectMapper();
        try {
            return Obj.writeValueAsString(activeScraper.getItems());
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }


    public void wgGesuchtScrapeCity(List<String> cityName){
        cityName.forEach(city->{
            activeScraper = new PageWgGesuchtDe("https://www.wg-gesucht.de/",
                    "autocompinp","formPortal","search_button",
                    "page");
            activeScraper.scrapeAllPages(city);
            activeScraper.quitBrowser();
        });
        killScraper();
    }
}
