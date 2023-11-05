package com.mse.datafabric.dataProducts.payload.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "dataProductId"})
public class RatingReponse implements Serializable
{
    private long id;
    private long dataProductId;
}