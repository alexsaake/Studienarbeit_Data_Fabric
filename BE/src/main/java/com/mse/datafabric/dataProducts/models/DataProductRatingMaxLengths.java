package com.mse.datafabric.dataProducts.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"title", "comment"})
public class DataProductRatingMaxLengths implements Serializable
{
    private int title;
    private int comment;
}