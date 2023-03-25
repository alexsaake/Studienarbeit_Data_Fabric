package com.mse.datafabric;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@JsonPropertyOrder({"shortKey", "title", "shortDescription", "lastUpdated", "category", "accessRights"})
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
    private DataProductCategories myCategory;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final DataProductOverviewBean other = (DataProductOverviewBean) obj;
        if (!Objects.equals(this.myShortKey, other.myShortKey)) {
            return false;
        }
        if (!Objects.equals(this.myTitle, other.myTitle)) {
            return false;
        }
        if (!Objects.equals(this.myShortDescription, other.myShortDescription)) {
            return false;
        }
        if (!Objects.equals(this.myLastUpdated, other.myLastUpdated)) {
            return false;
        }
        if (!Objects.equals(this.myCategory, other.myCategory)) {
            return false;
        }
        if (!Objects.equals(this.myAccessRight, other.myAccessRight)) {
            return false;
        }

        return true;
    }
}