package com.mse.datafabric.dataProducts.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"shortKey", "userName", "title", "comment", "rating", "submitted", "isEdited"})
public class DataProductRatingDto implements Serializable
{
    @Setter
    private String shortKey;
    @Setter
    private String userName;
    private String title;
    private String comment;
    private int rating;
    private Date submitted;
    private boolean isEdited;

    @JsonCreator
    public DataProductRatingDto(@JsonProperty("title")String title, @JsonProperty("comment")String comment, @JsonProperty("rating")int rating, @JsonProperty("submitted")Date submitted, @JsonProperty("isEdited")boolean isEdited) {
        this.title = title;
        this.comment = comment;
        this.rating = rating;
        this.submitted = submitted;
        this.isEdited = isEdited;
    }
}