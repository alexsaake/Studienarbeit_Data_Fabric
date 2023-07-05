package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductDetailDto;
import com.mse.datafabric.dataProducts.models.DataProductOverviewDto;
import com.mse.datafabric.dataProducts.models.DataProductRatingDto;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewDto> getDataProductsOverview();
    DataProductDetailDto getDataProductDetail(String shortKey);
    List<DataProductRatingDto> getDataProductsRating(String shortKey);
    void setDataProductsRating(DataProductRatingDto dataProductRating);
    boolean getHasAlreadyRatedDataProduct(String shortKey, String userName);
}
