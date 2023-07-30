package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductDetailDto;
import com.mse.datafabric.dataProducts.models.DataProductOverviewDto;
import com.mse.datafabric.dataProducts.models.DataProductRatingDto;
import com.mse.datafabric.dataProducts.models.DataProductRatingMaxLengths;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewDto> getDataProductsOverview();
    DataProductDetailDto getDataProductDetail(String shortKey);
    List<DataProductRatingDto> getDataProductRatings(String shortKey);
    DataProductRatingMaxLengths getDataProductRatingMaxLengths();
    boolean getDataProductRatingCanSubmit(String shortKey, String userName);
    void setDataProductsRating(DataProductRatingDto dataProductRating);
    void updateDataProductsRating(DataProductRatingDto dataProductRating);
    void markAsDeletedDataProductRating(String shortKey, String userName);
}
