package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;

@ShellComponent
@Component
class DataProductsService implements IDataProductsService
{
    JdbcTemplate myJdbcTemplate;
    @Autowired
    public DataProductsService(JdbcTemplate jdbcTemplate)
    {
        myJdbcTemplate = jdbcTemplate;
    }

    public List<DataProductOverviewDto> getDataProductsOverview()
    {
        String dataProductsSql = "SELECT id, title, lastUpdated, categoryId FROM DataProducts WHERE isDeleted = FALSE";
        List<Map<String, Object>> databaseDataProducts = myJdbcTemplate.queryForList(dataProductsSql);

        List<DataProductOverviewDto> dataProducts = new ArrayList<>();

        for (Map databaseDataProduct : databaseDataProducts)
        {
            DataProductOverviewDto dataProduct = new DataProductOverviewDto(
                (Long) databaseDataProduct.get("id"),
                (String) databaseDataProduct.get("title"),
                new Date(((Timestamp) databaseDataProduct.get("lastUpdated")).getTime()),
                (Long) databaseDataProduct.get("categoryId")
            );
            dataProducts.add(dataProduct);
        }

        return dataProducts;
    }

    public DataProductSummaryDto getDataProductSummary(long dataProductId) {
        String dataProductSql = "SELECT dp.imageFileName, dp.shortDescription, users.username, dp.accessRightId FROM DataProducts dp JOIN users ON dp.userId = users.id WHERE dp.id = '%s' AND dp.isDeleted = FALSE".formatted(dataProductId);
        Map<String, Object> databaseDataProduct = myJdbcTemplate.queryForMap(dataProductSql);

        return new DataProductSummaryDto(
                (String) databaseDataProduct.get("imageFileName"),
                (String) databaseDataProduct.get("shortDescription"),
                (String) databaseDataProduct.get("username"),
                (Long) databaseDataProduct.get("accessRightId")
        );
    }

    public DataProductDetailsDto getDataProductDetails(long dataProductId) {
        String dataProductSql = "SELECT description, source, sourceLink FROM DataProducts WHERE id = '%s' AND isDeleted = FALSE".formatted(dataProductId);
        Map<String, Object> databaseDataProduct = myJdbcTemplate.queryForMap(dataProductSql);

        return new DataProductDetailsDto(
            (String)databaseDataProduct.get("description"),
            (String)databaseDataProduct.get("source"),
            (String)databaseDataProduct.get("sourceLink")
        );
    }

    public void softDeleteDataProduct(long dataProductId) {
        String SOFT_DELETE_DATA_PRODUCT = "UPDATE DataProducts SET isDeleted = TRUE WHERE id = ?";
        myJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SOFT_DELETE_DATA_PRODUCT);
            ps.setLong(1, dataProductId);
            return ps;
        });
    }

    public List<RatingDto> getDataProductRatings(long dataProductId)
    {
        String ratingSql = "SELECT rate.id, rate.id_dataProducts FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id WHERE dp.id = '%s' AND rate.isDeleted = FALSE AND dp.isDeleted = FALSE".formatted(dataProductId);
        List<Map<String, Object>> databaseRatings = myJdbcTemplate.queryForList(ratingSql);

        List<RatingDto> ratings = new ArrayList<>();

        for (Map databaseRating : databaseRatings)
        {
            RatingDto dataProductRating = new RatingDto(
                    (Long) databaseRating.get("id"),
                    (Long) databaseRating.get("id_dataProducts")
            );
            ratings.add(dataProductRating);
        }

        return ratings;
    }

    public RatingDetailsDto getDataProductRatingDetails(long ratingId)
    {
        String ratingSql = "SELECT usr.userName, rate.title, rate.comment, rate.rating, rate.submitted, rate.isEdited FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN Users usr ON rate.id_users = usr.id WHERE rate.id = '%s' AND rate.isDeleted = FALSE AND dp.isDeleted = FALSE".formatted(ratingId);
        Map<String, Object> databaseRating = myJdbcTemplate.queryForMap(ratingSql);

        return new RatingDetailsDto(
                (String)databaseRating.get("userName"),
                (String)databaseRating.get("title"),
                (String)databaseRating.get("comment"),
                ((BigDecimal)databaseRating.get("rating")).intValue(),
                new Date(((Timestamp) databaseRating.get("submitted")).getTime()),
                (Boolean)databaseRating.get("isEdited")
        );
    }

    public DataProductRatingMaxLengths getDataProductRatingMaxLengths()
    {
        String dataProductsSql = "SELECT title, comment FROM DataProduct_Ratings";
        SqlRowSet rowSet = myJdbcTemplate.queryForRowSet(dataProductsSql);
        SqlRowSetMetaData metaData = rowSet.getMetaData();
        return new DataProductRatingMaxLengths(metaData.getPrecision(1), metaData.getPrecision(2));
    }

    public boolean getDataProductRatingCanSubmit(long dataProductId, String userName)
    {
        String dataProductsSql = "SELECT * FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN Users usr ON rate.id_users = usr.id WHERE dp.id = '%s' AND usr.userName = '%s' AND rate.isDeleted = FALSE and dp.isDeleted = FALSE".formatted(dataProductId, userName);
        List<Map<String, Object>> databaseDataProductsRating = myJdbcTemplate.queryForList(dataProductsSql);

        return databaseDataProductsRating.size() == 0;
    }

    public void setDataProductsRating(String userName, long dataProductId, RatingDetailsDto ratingDetails) {
        String dataProductsSql = "INSERT INTO DataProduct_Ratings (id_users, id_dataProducts, title, comment, rating) VALUES ((SELECT id FROM users WHERE userName = '%s'), '%s', '%s', '%s', %s)".formatted(userName, dataProductId, ratingDetails.getTitle(), ratingDetails.getComment(), ratingDetails.getRating());
        myJdbcTemplate.update(dataProductsSql);
    }

    public void updateDataProductsRating(long ratingId, RatingDetailsDto ratingDetails) {
        String dataProductsSql = "UPDATE DataProduct_Ratings SET title = '%s', comment = '%s', rating = %s, submitted = CURRENT_TIMESTAMP, isEdited = TRUE WHERE id = '%s' AND id_users = (SELECT id FROM Users WHERE userName = '%s') AND isDeleted = FALSE".formatted(ratingDetails.getTitle(), ratingDetails.getComment(), ratingDetails.getRating(), ratingId, ratingDetails.getUserName());
        myJdbcTemplate.update(dataProductsSql);
    }

    public void markAsDeletedDataProductRating(long ratingId) {
        String dataProductsSql = "UPDATE DataProduct_Ratings SET isDeleted = TRUE WHERE id = '%s' AND isDeleted = FALSE".formatted(ratingId);
        myJdbcTemplate.update(dataProductsSql);
    }
}
