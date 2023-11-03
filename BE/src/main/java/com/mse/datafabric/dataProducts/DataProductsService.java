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

    public DataProductSummaryDto getDataProductSummary(long id) {
        String dataProductSql = "SELECT dp.imageFileName, dp.shortDescription, users.username, dp.accessRightId FROM DataProducts dp JOIN users ON dp.userId = users.id WHERE dp.id = '%s' AND dp.isDeleted = FALSE".formatted(id);
        Map<String, Object> databaseDataProduct = myJdbcTemplate.queryForMap(dataProductSql);

        return new DataProductSummaryDto(
                (String) databaseDataProduct.get("imageFileName"),
                (String) databaseDataProduct.get("shortDescription"),
                (String) databaseDataProduct.get("username"),
                (Long) databaseDataProduct.get("accessRightId")
        );
    }

    public DataProductDetailDto getDataProductDetails(long id) {
        String dataProductSql = "SELECT description, source, sourceLink FROM DataProducts WHERE id = '%s' AND isDeleted = FALSE".formatted(id);
        Map<String, Object> databaseDataProduct = myJdbcTemplate.queryForMap(dataProductSql);

        return new DataProductDetailDto(
            (String)databaseDataProduct.get("description"),
            (String)databaseDataProduct.get("source"),
            (String)databaseDataProduct.get("sourceLink")
        );
    }

    public void softDeleteDataProduct(long id, String userName) {
        String SOFT_DELETE_DATA_PRODUCT = "UPDATE DataProducts SET isDeleted = TRUE WHERE id = ? AND isDeleted = FALSE AND (SELECT usr.username FROM users usr WHERE userId = usr.id) = ?";
        myJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SOFT_DELETE_DATA_PRODUCT);
            ps.setLong(1, id);
            ps.setString(2, userName);
            return ps;
        });
    }

    public List<RatingDto> getDataProductRatings(long id)
    {
        String dataProductsSql = "SELECT dp.id, usr.userName, rate.title, rate.comment, rate.rating, rate.submitted, rate.isEdited FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN Users usr ON rate.id_users = usr.id WHERE dp.id = '%s' AND rate.isDeleted = FALSE AND dp.isDeleted = FALSE".formatted(id);
        List<Map<String, Object>> databaseDataProductsRating = myJdbcTemplate.queryForList(dataProductsSql);

        List<RatingDto> dataProductsRating = new ArrayList<>();

        for (Map databaseDataProductRating : databaseDataProductsRating)
        {
            RatingDto dataProductRating = new RatingDto(
                    (Long) databaseDataProductRating.get("id"),
                    (String)databaseDataProductRating.get("userName"),
                    (String)databaseDataProductRating.get("title"),
                    (String)databaseDataProductRating.get("comment"),
                    ((BigDecimal)databaseDataProductRating.get("rating")).intValue(),
                    new Date(((Timestamp) databaseDataProductRating.get("submitted")).getTime()),
                    (Boolean)databaseDataProductRating.get("isEdited")
            );
            dataProductsRating.add(dataProductRating);
        }

        return dataProductsRating;
    }

    public DataProductRatingMaxLengths getDataProductRatingMaxLengths()
    {
        String dataProductsSql = "SELECT title, comment FROM DataProduct_Ratings";
        SqlRowSet rowSet = myJdbcTemplate.queryForRowSet(dataProductsSql);
        SqlRowSetMetaData metaData = rowSet.getMetaData();
        return new DataProductRatingMaxLengths(metaData.getPrecision(1), metaData.getPrecision(2));
    }

    public boolean getDataProductRatingCanSubmit(long id, String userName)
    {
        String dataProductsSql = "SELECT * FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN Users usr ON rate.id_users = usr.id WHERE dp.id = '%s' AND usr.userName = '%s' AND rate.isDeleted = FALSE and dp.isDeleted = FALSE".formatted(id, userName);
        List<Map<String, Object>> databaseDataProductsRating = myJdbcTemplate.queryForList(dataProductsSql);

        return databaseDataProductsRating.size() == 0;
    }

    public void setDataProductsRating(RatingDto dataProductRating) {
        String dataProductsSql = "INSERT INTO DataProduct_Ratings (id_users, id_dataProducts, title, comment, rating) VALUES ((SELECT id FROM users WHERE userName = '%s'), (SELECT id FROM DataProducts WHERE id = '%s' AND isDeleted = FALSE), '%s', '%s', %s)".formatted(dataProductRating.getUserName(), dataProductRating.getId(), dataProductRating.getTitle(), dataProductRating.getComment(), dataProductRating.getRating());
        myJdbcTemplate.update(dataProductsSql);
    }

    public void updateDataProductsRating(RatingDto dataProductRating) {
        String dataProductsSql = "UPDATE DataProduct_Ratings SET id_dataProducts = (SELECT id FROM DataProducts WHERE id = '%s'), id_users = (SELECT id FROM Users WHERE userName = '%s'), title = '%s', comment = '%s', rating = %s, submitted = CURRENT_TIMESTAMP, isEdited = TRUE WHERE id_dataProducts = (SELECT id FROM DataProducts WHERE id = '%s' AND isDeleted = FALSE) AND id_users = (SELECT id FROM Users WHERE userName = '%s') AND isDeleted = FALSE".formatted(dataProductRating.getId(), dataProductRating.getUserName(), dataProductRating.getTitle(), dataProductRating.getComment(), dataProductRating.getRating(), dataProductRating.getId(), dataProductRating.getUserName());
        myJdbcTemplate.update(dataProductsSql);
    }

    public void markAsDeletedDataProductRating(long id, String userName) {
        String dataProductsSql = "UPDATE DataProduct_Ratings SET isDeleted = TRUE WHERE id_dataProducts = (SELECT id FROM DataProducts WHERE id = '%s' AND isDeleted = FALSE) AND id_users = (SELECT id FROM Users WHERE userName = '%s')".formatted(id, userName);
        myJdbcTemplate.update(dataProductsSql);
    }
}
