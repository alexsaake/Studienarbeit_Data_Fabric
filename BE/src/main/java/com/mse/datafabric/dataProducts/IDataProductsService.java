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
    void setDataProductsRating(DataProductRatingDto dataProductRating);
    boolean getDataProductRatingCanSubmit(String shortKey, String userName);
}
