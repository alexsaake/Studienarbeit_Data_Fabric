package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.*;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewDto> getDataProductsOverview();
    DataProductSummaryDto getDataProductSummary(long id);
    DataProductDetailDto getDataProductDetails(long id);
    void softDeleteDataProduct(long id, String userName);
    List<RatingDto> getDataProductRatings(long id);
    DataProductRatingMaxLengths getDataProductRatingMaxLengths();
    boolean getDataProductRatingCanSubmit(long id, String userName);
    void setDataProductsRating(RatingDto dataProductRating);
    void updateDataProductsRating(RatingDto dataProductRating);
    void markAsDeletedDataProductRating(long id, String userName);
}
