package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductDetailDto;
import com.mse.datafabric.dataProducts.models.DataProductOverviewDto;
import com.mse.datafabric.dataProducts.models.RatingDto;
import com.mse.datafabric.dataProducts.models.DataProductRatingMaxLengths;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewDto> getDataProductsOverview();
    DataProductDetailDto getDataProductDetail(String shortKey);
    List<RatingDto> getDataProductRatings(String shortKey);
    DataProductRatingMaxLengths getDataProductRatingMaxLengths();
    boolean getDataProductRatingCanSubmit(String shortKey, String userName);
    void setDataProductsRating(RatingDto dataProductRating);
    void updateDataProductsRating(RatingDto dataProductRating);
    void markAsDeletedDataProductRating(String shortKey, String userName);
}
