package com.mse.datafabric.immobilien.webScraper.wgGesuchtDE;


import com.mse.datafabric.immobilien.webScraper.ScrapingDom;
import org.jsoup.select.Elements;

public class DomWgGesuchtDe extends ScrapingDom {
    public DomWgGesuchtDe(String domContent, String itemId, String city) {
        super(domContent, itemId, city);
    }

    public String getContentByPathX(String pathXContent){
        Elements elementContent = domContentDocument.select(pathXContent);
        if(elementContent.size()==0)
           return null;
        return elementContent.get(0).wholeText().trim();
    }
    @Override
    public String getPortalId(){
        return "wgGesucht";
    }
    @Override
    public String getFlatSize() {return null;}
    @Override
    public String getRent(){return null;}
    @Override
    public String getExtraCharges(){return null;}
    @Override
    public String getRoomSize(){return null;}
    @Override
    public String getDeposit(){return null;}
    @Override
    public String getFrom(){return null;}
    @Override
    public String getBorough(){return null;}
    @Override
    public String getFurnished(){return null;}
    @Override
    public String getStatus(){return null;}
    @Override
    public String getCreationDate(){return null;}
    @Override
    public String getTitle(){
        String op1 =  getContentByPathX("span[class='headlineContent']");
        if(op1 != null)
            return op1;
        String op2 =  getContentByPathX("h3[class='headline headline-detailed-view-title']");
        if(op2 != null)
            return op2;
        return getContentByPathX("h1[id='sliderTopTitle']");
    }
}
