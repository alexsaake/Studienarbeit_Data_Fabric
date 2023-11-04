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
@JsonPropertyOrder({"userName", "title", "comment", "rating", "submitted", "isEdited"})
public class RatingDetailsDto implements Serializable {
    private String userName;
    private String title;
    private String comment;
    private int rating;
    private Date submitted;
    private boolean isEdited;

    @JsonCreator
    public RatingDetailsDto(@JsonProperty("title")String title, @JsonProperty("comment")String comment, @JsonProperty("rating")int rating) {
        this.title = title;
        this.comment = comment;
        this.rating = rating;
    }
}