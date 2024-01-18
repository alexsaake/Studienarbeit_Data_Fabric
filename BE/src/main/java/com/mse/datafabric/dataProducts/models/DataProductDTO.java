package com.mse.datafabric.dataProducts.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Bytes;

import java.util.Date;

public class DataProductDTO {
    public long id;
    public String title;
    public String description;
    public String shortDescription;
    public Date lastUpdated;
    public int accessRightId;
    public int categoryId;
    public String source;
    public String sourceLink;
    public String data;
    public java.sql.Date createdOn;

    public String username;

    public byte[] image;
    @JsonCreator
    public DataProductDTO(@JsonProperty("id")long id, @JsonProperty("title")String title, @JsonProperty("description")String description,
                          @JsonProperty("shortDescription")String shortDescription, @JsonProperty("source")String source, @JsonProperty("sourceLink")String sourceLink,
                          @JsonProperty("accessRight")int accessRightId, @JsonProperty("category")int categoryId, @JsonProperty("data") Object data,
                          @JsonProperty("username") String username, @JsonProperty("createdon") java.sql.Date createdOn, @JsonProperty("image") byte[] image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.shortDescription = shortDescription;
        this.source = source;
        this.sourceLink = sourceLink;
        this.accessRightId = accessRightId;
        this.categoryId = categoryId;
        this.username = username;
        this.createdOn = createdOn;
        this.image = image;
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.data = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
