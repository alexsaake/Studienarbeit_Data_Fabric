package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductDetailDto;
import com.mse.datafabric.dataProducts.models.DataProductOverviewDto;
import com.mse.datafabric.dataProducts.models.DataProductRatingDto;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewDto> getDataProductsOverview();
    DataProductDetailDto getDataProductDetail(String shortKey);
    List<DataProductRatingDto> getDataProductRatings(String shortKey);
    int getDataProductRatingCommentMaxLength();
    boolean getDataProductRatingCanSubmit(String shortKey, String userName);
    void setDataProductsRating(DataProductRatingDto dataProductRating);
    void markAsDeletedDataProductRating(String shortKey, String userName);
}
