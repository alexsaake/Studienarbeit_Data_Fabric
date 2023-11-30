package com.mse.datafabric.dataProducts.payload.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"imageFileName", "shortDescription", "accessRightsId"})
public class DataProductSummaryResponse implements Serializable
{
    private String imageFileName;
    private String shortDescription;
    private long accessRightId;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final DataProductSummaryResponse other = (DataProductSummaryResponse) obj;
        if (!Objects.equals(this.imageFileName, other.imageFileName)) {
            return false;
        }
        if (!Objects.equals(this.shortDescription, other.shortDescription)) {
            return false;
        }
        if (!Objects.equals(this.accessRightId, other.accessRightId)) {
            return false;
        }

        return true;
    }
}