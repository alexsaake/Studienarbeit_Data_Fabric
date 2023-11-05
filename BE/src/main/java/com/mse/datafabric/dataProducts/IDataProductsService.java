package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.payload.RatingDetailsDTO;
import com.mse.datafabric.dataProducts.payload.response.*;

import java.util.List;

public interface IDataProductsService
{
    List<DataProductOverviewResponse> getDataProductsOverview();
    DataProductSummaryReponse getDataProductSummary(long dataProductId);
    DataProductDetailsReponse getDataProductDetails(long dataProductId);
    boolean softDeleteDataProduct(String userName, long dataProductId);
    List<RatingReponse> getDataProductRatings(long dataProductId);
    RatingDetailsDTO getDataProductRatingDetails(long ratingId);
    RatingMaxLengthsResponse getDataProductRatingMaxLengths();
    boolean getDataProductRatingCanSubmit(String userName, long dataProductId);
    boolean setDataProductsRating(String userName, long dataProductId, RatingDetailsDTO ratingDetails);
    boolean updateDataProductsRating(String userName, long ratingId, RatingDetailsDTO ratingDetails);
    boolean markAsDeletedDataProductRating(String userName, long ratingId);
}
