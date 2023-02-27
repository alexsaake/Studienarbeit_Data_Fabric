package com.mse.datafabric;

import java.io.Serializable;
import java.util.Date;

class DataProductBean implements Serializable, IDataProductBean {
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

    @Override
    public String getDataProductKey() {
        return myDataProductKey;
    }

    @Override
    public void setDataProductKey(String dataProductKey) {
myDataProductKey= dataProductKey;
    }

    private String myDataProductKey;

    public DataProductBean(){
        myTitle = null;
        myShortDescription = null;
        myLastUpdated = null;
        myDataProductAccessRights = null;
        myDataProductKey = null;
    }
}