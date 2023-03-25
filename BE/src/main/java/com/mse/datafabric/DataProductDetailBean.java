package com.mse.datafabric;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@JsonPropertyOrder({"shortKey", "title", "shortDescription", "description", "source", "sourceLink", "lastUpdated", "category", "accessRights"})
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final DataProductDetailBean other = (DataProductDetailBean) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (!Objects.equals(this.myDescription, other.myDescription)) {
            return false;
        }
        if (!Objects.equals(this.mySource, other.mySource)) {
            return false;
        }
        if (!Objects.equals(this.mySourceLink, other.mySourceLink)) {
            return false;
        }

        return true;
    }
}