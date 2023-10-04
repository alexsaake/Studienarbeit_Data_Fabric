package com.mse.datafabric.dataProducts.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonToken;
import org.json.JSONArray;

import java.util.Date;

public class DataProductDTO {
    public String shortKey;
    public String title;
    public String description;
    public String shortDescription;
    public Date lastUpdated;
    public int accessRightId;
    public int categoryId;
    public String source;
    public String sourceLink;
    public String data;

    public String username;
    @JsonCreator
    public DataProductDTO(@JsonProperty("shortKey")String shortKey, @JsonProperty("title")String title, @JsonProperty("description")String description,
                          @JsonProperty("shortDescription")String shortDescription, @JsonProperty("source")String source, @JsonProperty("sourceLink")String sourceLink,
                          @JsonProperty("accessRight")int accessRightId, @JsonProperty("category")int categoryId, @JsonProperty("data") Object data,
                          @JsonProperty("username") String username) {
        this.shortKey = shortKey;
        this.title = title;
        this.description = description;
        this.shortDescription = shortDescription;
        this.source = source;
        this.sourceLink = sourceLink;
        this.accessRightId = accessRightId;
        this.categoryId = categoryId;
        this.username = username;
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.data = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
