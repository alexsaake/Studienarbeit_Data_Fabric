package com.mse.datafabric.immobilien.webScraper.immoscout24De;


import com.mse.datafabric.immobilien.webScraper.ScrapingDom;
import com.mse.datafabric.immobilien.webScraper.dtos.CityItemDTO;
import org.jsoup.select.Elements;

public class DomImmoscout24De extends ScrapingDom {
    public DomImmoscout24De(CityItemDTO dto) {
        super(dto);
    }

    public String parseContent(String content, boolean split)    {
        String[] splitContent = content.split("\n");
        if(!split){
            String returnContent = "";
            for(int sc = 0;sc < splitContent.length; sc++){
                returnContent += splitContent[sc].trim();
                if (splitContent[sc].trim() != "" && splitContent.length > (sc+1) && splitContent[sc+1].trim() != "")
                    returnContent+=" ";
            }
            return returnContent;
        }
        if (splitContent.length > 2){
            return splitContent[2].trim();
        }
        if (splitContent.length > 1){
            return splitContent[1].trim();
        }
        return null;
    }
    public String getContentByPathX(String pathXContent, boolean split){
        Elements elementContent = domContentDocument.select(pathXContent);
        if(elementContent.size()==0)
           return null;
        return parseContent(elementContent.get(0).wholeText(),split);
    }
    @Override
    public String getPortalId(){
        return "wgSuche";
    }
    @Override
    public String getFlatSize() {return getContentByPathX("div[class='odp-dataset__content']>div[ng-if='offer.flatSize']",true);}
    @Override
    public String getRent(){return getContentByPathX("div[class='odp-dataset__content']>div[ng-if='offer.rent']",true);}
    @Override
    public String getExtraCharges(){return getContentByPathX("div[class='odp-dataset__content']>div[ng-if='offer.extraCharges']",true);}
    @Override
    public String getRoomSize(){return getContentByPathX("div[class='odp-dataset__content']>div[ng-if='offer.size']",true);}
    @Override
    public String getDeposit(){return getContentByPathX("div[class='odp-dataset__content']>div[ng-if='offer.deposit']",true);}
    @Override
    public String getFrom(){
        String from1 = getContentByPathX("div[class='odp-dataset__content']>div>span[ng-if='offer.from']",false);
        String from2 = getContentByPathX("div[class='odp-dataset__content']>div>span[ng-if='!offer.from']",false);
        if (from1 != null)
            return from1;
        else
            return from2;
    }
    @Override
    public String getBorough(){return null;}
    @Override
    public String getFurnished(){
        return getContentByPathX("div[class='odp-dataset__content']>div>span[translate^='userinput.offer.room.furnishedtype']",false);}
    @Override
    public String getStatus(){return null;}
    @Override
    public String getCreationDate(){return null;}
    @Override
    public String getTitle(){return getContentByPathX("div[class='odp-title']>h1",false);}
}
