package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductDetailDto;
import com.mse.datafabric.dataProducts.models.DataProductOverviewDto;
import com.mse.datafabric.dataProducts.models.RatingDto;
import com.mse.datafabric.dataProducts.models.DataProductRatingMaxLengths;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewDto> getDataProductsOverview();
    DataProductDetailDto getDataProductDetail(int id);
    void softDeleteDataProduct(int id, String userName);
    List<RatingDto> getDataProductRatings(int id);
    DataProductRatingMaxLengths getDataProductRatingMaxLengths();
    boolean getDataProductRatingCanSubmit(int id, String userName);
    void setDataProductsRating(RatingDto dataProductRating);
    void updateDataProductsRating(RatingDto dataProductRating);
    void markAsDeletedDataProductRating(int id, String userName);
}
