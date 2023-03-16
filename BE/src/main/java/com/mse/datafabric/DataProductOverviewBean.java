package com.mse.datafabric;

import java.io.Serializable;
import java.util.Date;

class DataProductOverviewBean implements Serializable
{
    protected String myShortKey;
    public String getShortKey() {
        return  myShortKey;
    }
    protected String myTitle;
    public String getTitle() {
        return  myTitle;
    }
    protected String myShortDescription;
    public String getShortDescription(){
        return myShortDescription;
    }
    protected Date myLastUpdated;
    public Date getLastUpdated(){
        return myLastUpdated;
    }
    protected DataProductAccessRights myAccessRight;
    public DataProductAccessRights getAccessRight(){
        return myAccessRight;
    }
    private final DataProductCategories myCategory;
    public DataProductCategories getCategory() {
        return  myCategory;
    }

    public DataProductOverviewBean(String shortKey, String title, String shortDescription, Date lastUpdated, DataProductCategories category, DataProductAccessRights accessRights)
    {
        myShortKey = shortKey;
        myTitle = title;
        myShortDescription = shortDescription;
        myLastUpdated = lastUpdated;
        myCategory = category;
        myAccessRight = accessRights;
    }
}