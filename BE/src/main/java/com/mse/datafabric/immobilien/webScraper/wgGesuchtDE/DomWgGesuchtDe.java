package com.mse.datafabric.immobilien.webScraper.wgGesuchtDE;


import com.mse.datafabric.immobilien.webScraper.ScrapingDom;
import com.mse.datafabric.immobilien.webScraper.dtos.CityItemDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String removeUnit(String unitContent){
        if (unitContent==null)
            return null;
        return unitContent.replace("€","").replace("m²","").trim();
    }
    @Override
    public String getPortalId(){
        return "wgGesucht";
    }
    @Override
    public String getFlatSize() {return null;}
    @Override
    public String getRent(){return removeUnit(getContentNextSibling(domContentDocument,"td:containsOwn(Miete:)","td>b"));}
    @Override
    public String getExtraCharges(){return removeUnit(getContentNextSibling(domContentDocument,"td:containsOwn(Nebenkosten:)","td>b"));}
    @Override
    public String getRoomSize(){return removeUnit(getContentPreviousSibling(domContentDocument,allHeadingsContain("Größe"),"h1,h2,h3,h4"));}
    @Override
    public String getDeposit(){return removeUnit(getContentNextSibling(domContentDocument,"td:containsOwn(Kaution:)","td>b"));}
    @Override
    public String getFrom(){
        String content = getContentNextSibling(domContentDocument,"h3:containsOwn(Verfügbarkeit)","p>b");
        String[] splitContent = content.split("\\n");
        String dateString = null;
        for(int sc = 0;sc < splitContent.length; sc++){
            if(splitContent[sc].equals("frei ab:") && sc+1 <= splitContent.length ){
                dateString = splitContent[sc+1].trim();
            }
        }
        if (dateString != null){
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date;
            try {
                date = inputFormat.parse(dateString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return outputFormat.format(date);
        }
        return null;
    }
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
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");

        Date date = new Date();

        String DateString = getContentByPathX(domItemCardContentDocument,"span:containsOwn(Online:)");
        DateString = DateString.replace("Online: ","");
        if(DateString.contains("Stunde")||DateString.contains("Minute")){
            return outputFormat.format(date);
        }
        if(DateString.contains("Tag")){
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -Integer.parseInt(DateString.substring(0,1)));
            date = cal.getTime();
            return outputFormat.format(date);
        }

        try {
            date = inputFormat.parse(DateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputFormat.format(date);
    }
    @Override
    public String getTitle(){
        return  getContentByPathX(domContentDocument,"title");
    }
    @Override
    public String getAddressCity(){
        String content = getContentNextSibling(domContentDocument,"h3:matchesOwn(Adresse)","a");
        String[] splitContent = content.split("\\n");
        if(splitContent.length < 3)
            return null;
        return splitContent[3].trim();
    }
    @Override
    public String getAddressStreet(){
        String content = getContentNextSibling(domContentDocument,"h3:matchesOwn(Adresse)","a");
        String[] splitContent = content.split("\\n");
        if(splitContent.length < 3)
            return null;
        return splitContent[0].trim();
    }
    @Override
    public String getCurrencyUnit(){
        return "€";
    }
    @Override
    public String getSizeUnit(){
        return "m²";
    }

}
