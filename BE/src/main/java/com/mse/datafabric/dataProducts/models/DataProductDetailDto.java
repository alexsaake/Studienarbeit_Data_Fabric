package com.mse.datafabric.dataProducts.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@JsonPropertyOrder({"shortKey", "title", "shortDescription", "description", "source", "sourceLink", "lastUpdated", "category", "accessRights"})
public class DataProductDetailDto extends DataProductOverviewDto implements Serializable
{
    private String description;
    private String source;
    private String sourceLink;

    public DataProductDetailDto(String shortKey, String title, String shortDescription, Date lastUpdated, DataProductAccessRights accessRight, DataProductCategories category, String description, String source, String sourceLink, String userName) {
        super(shortKey, title, shortDescription, lastUpdated, userName, accessRight, category);
        this.description = description;
        this.source = source;
        this.sourceLink = sourceLink;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final DataProductDetailDto other = (DataProductDetailDto) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.sourceLink, other.sourceLink)) {
            return false;
        }

        return true;
    }
}