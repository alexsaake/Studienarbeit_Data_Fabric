package com.mse.datafabric;

import java.io.Serializable;
import java.util.Date;

class DataProductDetailBean extends DataProductOverviewBean implements Serializable
{
    private final String myDescription;
    public String getDescription() {
        return  myDescription;
    }
    private final String mySource;
    public String getSource() {
        return mySource;
    }
    private final String mySourceLink;
    public String getSourceLink() {
        return mySourceLink;
    }

    public DataProductDetailBean(String shortKey, String title, String shortDescription, String description, String source, String sourceLink, Date lastUpdated, DataProductCategories category, DataProductAccessRights accessRights)
    {
        super(shortKey, title, shortDescription, lastUpdated, category, accessRights);
        myDescription = description;
        mySource = source;
        mySourceLink = sourceLink;
    }
}