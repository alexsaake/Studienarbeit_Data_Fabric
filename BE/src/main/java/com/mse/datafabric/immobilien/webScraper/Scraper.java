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
    @ShellMethod( "runScraper" )
    public void runScraper() {
        wgGesuchtScrapeCity(Arrays.asList("Nürnberg"));
    }

    public void wgSucheScrapeCity(List<String> cityName){
        cityName.forEach(city->{
            PageWgSucheDe scrape01 = new PageWgSucheDe("https://www.wg-suche.de/",
                    "homeSearchLocation","HomeSelectionForm",null,
                    "page");
            scrape01.scrapeAllPages(city);
            scrape01.quitBrowser();
        });
    }
    public void wgGesuchtScrapeCity(List<String> cityName){
        cityName.forEach(city->{
            PageWgGesuchtDe scrape01 = new PageWgGesuchtDe("https://www.wg-gesucht.de/",
                    "autocompinp","formPortal","search_button",
                    "page");
            scrape01.scrapeAllPages(city);
            scrape01.quitBrowser();
        });
    }
//    public void immoscout24DeScrapeCity(List<String> cityName){
//        cityName.forEach(city->{
//            PageImmoscout24De scrape01 = new PageImmoscout24De("https://www.immobilienscout24.de/",
//                    "oss-location","oss-form",null,
//                    "page");
//            scrape01.scrapeAllPages(city);
//            scrape01.quitBrowser();
//        });
//    }
}
