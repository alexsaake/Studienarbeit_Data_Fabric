package com.mse.datafabric.dataProducts.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "title", "shortDescription", "lastUpdated", "userName", "category", "accessRights"})
public class DataProductOverviewDto implements Serializable
{
    protected int id;
    protected String title;
    protected String shortDescription;
    protected Date lastUpdated;
    protected String userName;
    protected DataProductAccessRights accessRight;
    private DataProductCategories category;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final DataProductOverviewDto other = (DataProductOverviewDto) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.shortDescription, other.shortDescription)) {
            return false;
        }
        if (!Objects.equals(this.lastUpdated, other.lastUpdated)) {
            return false;
        }
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        if (!Objects.equals(this.accessRight, other.accessRight)) {
            return false;
        }

        return true;
    }
}