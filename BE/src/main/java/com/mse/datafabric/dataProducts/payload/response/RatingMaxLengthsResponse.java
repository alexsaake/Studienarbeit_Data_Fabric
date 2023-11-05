package com.mse.datafabric.dataProducts.payload.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"title", "comment"})
public class RatingMaxLengthsResponse implements Serializable
{
    private int title;
    private int comment;
}