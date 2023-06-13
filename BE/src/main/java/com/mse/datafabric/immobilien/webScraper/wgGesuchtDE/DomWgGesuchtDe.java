package com.mse.datafabric.immobilien.webScraper.wgGesuchtDE;


import com.mse.datafabric.immobilien.webScraper.ScrapingDom;
import com.mse.datafabric.immobilien.webScraper.dtos.CityItemDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DomWgGesuchtDe extends ScrapingDom {
    public DomWgGesuchtDe(CityItemDTO dto) {
        super(dto);
    }

    public String getContentByPathX(Document doc,String pathXContent){
        Elements elementContent = doc.select(pathXContent);
        if(elementContent.size()==0)
           return null;
        return elementContent.get(0).wholeText().trim();
    }
    public String getContentNextSibling(Document doc,String pathXContent1, String pathXContent2){
        Elements elementContent = doc.select(pathXContent1);
        if(elementContent.size()==0)
            return null;
        Element element2Content = elementContent.get(0).nextElementSibling();
        element2Content.select(pathXContent2);
        return element2Content.wholeText().trim();
    }
    public String getContentPreviousSibling(Document doc, String pathXContent1, String pathXContent2){
        Elements elementContent = doc.select(pathXContent1);
        if(elementContent.size()==0)
            return null;
        Element element2Content = elementContent.get(0).previousElementSibling();
        element2Content.select(pathXContent2);
        return element2Content.wholeText().trim();
    }
    public String allHeadingsContain(String text){
        return "h1:containsOwn("+text+"),h2:containsOwn("+text+"),h3:containsOwn("+text+"),h4:containsOwn("+text+")";
    }
    @Override
    public String getPortalId(){
        return "wgGesucht";
    }
    @Override
    public String getFlatSize() {return null;}
    @Override
    public String getRent(){return getContentNextSibling(domContentDocument,"td:containsOwn(Miete:)","td>b");}
    @Override
    public String getExtraCharges(){return getContentNextSibling(domContentDocument,"td:containsOwn(Nebenkosten:)","td>b");}
    @Override
    public String getRoomSize(){return getContentPreviousSibling(domContentDocument,allHeadingsContain("Größe"),"h1,h2,h3,h4");}
    @Override
    public String getDeposit(){return getContentNextSibling(domContentDocument,"td:containsOwn(Kaution:)","td>b");}
    @Override
    public String getFrom(){return getContentNextSibling(domContentDocument,"h3:containsOwn(Verfügbarkeit)","p>b");}
    @Override
    public String getBorough(){return null;}
    @Override
    public String getFurnished(){return null;}
    @Override
    public String getStatus(){
        if(getContentByPathX(domItemCardContentDocument,"span:containsOwn(vermietet)") == null)
            return "active";
        return "rented";
    }
    @Override
    public String getCreationDate(){
        String DateString = getContentByPathX(domItemCardContentDocument,"span:containsOwn(Online:)");
        DateString = DateString.replace("Online: ","");
        if(DateString.contains("Stunde")){
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            return formatter.format(date);
        }
        return DateString;
    }
    @Override
    public String getTitle(){
        return  getContentByPathX(domContentDocument,"title");
    }
}
