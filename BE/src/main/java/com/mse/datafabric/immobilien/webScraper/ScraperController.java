package com.mse.datafabric.immobilien.webScraper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;

@RestController
@ShellComponent
@RequestMapping("/api/Gateway")
public class ScraperController {
    @Autowired
    private Scraper scraper;

    @ShellMethod( "getScraperData" )
    @GetMapping(
            value = "/ScraperData",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getScraperData(){
        JSONObject jo = new JSONObject("{items:"+scraper.getItems()+"}");
        jo.put("status", scraper.getStatus());
        jo.put("itemCount", scraper.getItemCount());
        jo.put("pageCount", scraper.getPageCount());

        return jo.toString();
    }
    @PostMapping(
            value = "/ScraperData",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String startScraper(@RequestParam(value="city") String city){
        JSONObject data = new JSONObject();

        JSONObject jo = new JSONObject();
        jo.put("status", scraper.start(city));
        return jo.toString();
    }
    @PatchMapping(
            value = "/ScraperData",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String stopScraper(@RequestParam(value="status") String status){
        JSONObject jo = new JSONObject();
        switch (status)
        {
            case "stop":
                scraper.stopScraper();
                jo.put("status", scraper.getStatus());
            break;
        }
        return jo.toString();
    }
}
