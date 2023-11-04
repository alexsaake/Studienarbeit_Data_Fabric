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
@JsonPropertyOrder({"id", "dataProductId"})
public class RatingDto implements Serializable
{
    private long id;
    private long dataProductId;
}