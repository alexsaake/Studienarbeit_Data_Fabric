package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.*;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewDto> getDataProductsOverview();
    DataProductSummaryDto getDataProductSummary(long dataProductId);
    DataProductDetailsDto getDataProductDetails(long dataProductId);
    void softDeleteDataProduct(String userName, long dataProductId);
    List<RatingDto> getDataProductRatings(long dataProductId);
    RatingDetailsDto getDataProductRatingDetails(long ratingId);
    DataProductRatingMaxLengths getDataProductRatingMaxLengths();
    boolean getDataProductRatingCanSubmit(String userName, long dataProductId);
    void setDataProductsRating(String userName, long dataProductId, RatingDetailsDto ratingDetails);
    void updateDataProductsRating(String userName, long ratingId, RatingDetailsDto ratingDetails);
    void markAsDeletedDataProductRating(String userName, long ratingId);
}
