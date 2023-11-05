package com.mse.datafabric.dataProducts.payload.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"description", "source", "sourceLink"})
public class DataProductDetailsReponse implements Serializable
{
    private final String description;
    private final String source;
    private final String sourceLink;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final DataProductDetailsReponse other = (DataProductDetailsReponse) obj;
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