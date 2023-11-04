package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.*;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewDto> getDataProductsOverview();
    DataProductSummaryDto getDataProductSummary(long dataProductId);
    DataProductDetailsDto getDataProductDetails(long dataProductId);
    void softDeleteDataProduct(long dataProductId);
    List<RatingDto> getDataProductRatings(long dataProductId);
    RatingDetailsDto getDataProductRatingDetails(long ratingId);
    DataProductRatingMaxLengths getDataProductRatingMaxLengths();
    boolean getDataProductRatingCanSubmit(long dataProductId, String userName);
    void setDataProductsRating(String userName, long dataProductId, RatingDetailsDto ratingDetails);
    void updateDataProductsRating(long ratingId, RatingDetailsDto ratingDetails);
    void markAsDeletedDataProductRating(long ratingId);
}
