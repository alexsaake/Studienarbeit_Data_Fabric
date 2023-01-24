package com.mse.datafabric;

import java.io.Serializable;
import java.util.Date;

class DataProductBean implements Serializable, IDataProductBean {
    private byte[] myImage;
    public byte[] getImage(){
        return  myImage;
    }
    public void setImage(byte[] image){
        myImage = image;
    }
    private  String myTitle;
    public String getTitle() {
        return  myTitle;
    }
    public void setTitle(String title){
        myTitle = title;
    }
    private  String myShortDescription;
    public String getShortDescription(){
        return myShortDescription;
    }
    public void setShortDescription(String shortDescription){
        myShortDescription = shortDescription;
    }
    private Date myLastUpdated;
    public Date getLastUpdated(){
        return myLastUpdated;
    }
    public void setLastUpdated(Date lastUpdated){
        myLastUpdated = lastUpdated;
    }
    private DataProductAccessRights myDataProductAccessRights;
    public DataProductAccessRights getAccessRights(){
        return myDataProductAccessRights;
    }
    public void setAccessRights(DataProductAccessRights dataProductAccessRights){
        myDataProductAccessRights = dataProductAccessRights;
    }

    public DataProductBean(){
        myImage = null;
        myTitle = null;
        myShortDescription = null;
        myLastUpdated = null;
        myDataProductAccessRights = null;
    }
}