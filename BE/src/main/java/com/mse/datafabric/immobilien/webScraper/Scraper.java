package com.mse.datafabric.immobilien.webScraper;

import com.mse.datafabric.immobilien.webScraper.immoscout24De.PageImmoscout24De;
import com.mse.datafabric.immobilien.webScraper.wgGesuchtDE.PageWgGesuchtDe;
import com.mse.datafabric.immobilien.webScraper.wgSucheDe.PageWgSucheDe;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Arrays;
import java.util.List;


@ShellComponent
public class Scraper {

    private ScrapingPage activeScraper;

    @ShellMethod( "runScraper" )
    public void runScraper() {
        wgGesuchtScrapeCity(Arrays.asList("Nürnberg"));
    }
    public String getStatus(){
        return activeScraper.getStatus();
    }
    public int getPageCount(){
        return activeScraper.getPageCount();
    }

    public void wgGesuchtScrapeCity(List<String> cityName){
        cityName.forEach(city->{
            activeScraper = new PageWgGesuchtDe("https://www.wg-gesucht.de/",
                    "autocompinp","formPortal","search_button",
                    "page");
            activeScraper.scrapeAllPages(city);
            activeScraper.quitBrowser();
        });
    }
}
