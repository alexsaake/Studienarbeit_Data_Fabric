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
        String dataProductsSql = "SELECT dp.shortKey, dp.title, dp.shortDescription, dp.lastUpdated, dpc.category, dpar.accessRight FROM DataProducts dp JOIN DataProduct_Categories dpc ON dp.categoryId = dpc.id JOIN DataProduct_AccessRights dpar ON dp.accessRightId = dpar.id";
        List<Map<String, Object>> databaseDataProducts = myJdbcTemplate.queryForList(dataProductsSql);

        List<DataProductOverviewDto> dataProducts = new ArrayList<>();

        for (Map databaseDataProduct : databaseDataProducts)
        {
            DataProductOverviewDto dataProduct = new DataProductOverviewDto(
                (String)databaseDataProduct.get("shortKey"),
                (String)databaseDataProduct.get("title"),
                (String)databaseDataProduct.get("shortDescription"),
                new Date(((Timestamp) databaseDataProduct.get("lastUpdated")).getTime()),
                DataProductAccessRights.valueOf((String)databaseDataProduct.get("accessRight")),
                DataProductCategories.valueOf((String)databaseDataProduct.get("category"))
            );
            dataProducts.add(dataProduct);
        }

        return dataProducts;
    }

    public DataProductDetailDto getDataProductDetail(String shortKey) {
        String dataProductSql = "SELECT dp.title, dp.shortDescription, dp.description, dp.source, dp.sourceLink, dp.lastUpdated, dpc.category, dpar.accessRight FROM DataProducts dp JOIN DataProduct_Categories dpc ON dp.categoryId = dpc.id JOIN DataProduct_AccessRights dpar ON dp.accessRightId = dpar.id WHERE dp.shortKey = '%s'".formatted(shortKey);
        Map<String, Object> databaseDataProduct = myJdbcTemplate.queryForMap(dataProductSql);

        return new DataProductDetailDto(
            shortKey,
            (String)databaseDataProduct.get("title"),
            (String)databaseDataProduct.get("shortDescription"),
            new Date(((Timestamp) databaseDataProduct.get("lastUpdated")).getTime()),
            DataProductAccessRights.valueOf((String)databaseDataProduct.get("accessRight")),
            DataProductCategories.valueOf((String)databaseDataProduct.get("category")),
            (String)databaseDataProduct.get("description"),
            (String)databaseDataProduct.get("source"),
            (String)databaseDataProduct.get("sourceLink")
        );
    }

    public List<DataProductRatingDto> getDataProductRatings(String shortKey)
    {
        String dataProductsSql = "SELECT dp.shortKey, usr.userName, rate.title, rate.comment, rate.rating, rate.submitted, rate.isEdited FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN User usr ON rate.id_users = usr.id WHERE dp.shortKey = '%s' AND rate.isDeleted = FALSE".formatted(shortKey);
        List<Map<String, Object>> databaseDataProductsRating = myJdbcTemplate.queryForList(dataProductsSql);

        List<DataProductRatingDto> dataProductsRating = new ArrayList<>();

        for (Map databaseDataProductRating : databaseDataProductsRating)
        {
            DataProductRatingDto dataProductRating = new DataProductRatingDto(
                    (String)databaseDataProductRating.get("shortKey"),
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

    public boolean getDataProductRatingCanSubmit(String shortKey, String userName)
    {
        String dataProductsSql = "SELECT * FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN User usr ON rate.id_users = usr.id WHERE dp.shortKey = '%s' AND usr.username = '%s' AND rate.isDeleted = FALSE".formatted(shortKey, userName);
        List<Map<String, Object>> databaseDataProductsRating = myJdbcTemplate.queryForList(dataProductsSql);

        return databaseDataProductsRating.size() == 0;
    }

    public void setDataProductsRating(DataProductRatingDto dataProductRating) {
        String dataProductsSql = "INSERT INTO DataProduct_Ratings (id_users, id_dataProducts, title, comment, rating) VALUES (SELECT id FROM user WHERE username = '%s', SELECT id FROM DataProducts WHERE shortKey = '%s', '%s', '%s', %s)".formatted(dataProductRating.getUserName(), dataProductRating.getShortKey(), dataProductRating.getTitle(), dataProductRating.getComment(), dataProductRating.getRating());
        myJdbcTemplate.update(dataProductsSql);
    }

    public void updateDataProductsRating(DataProductRatingDto dataProductRating) {
        String dataProductsSql = "UPDATE DataProduct_Ratings SET id_dataProducts = (SELECT id FROM DataProducts WHERE shortKey = '%s'), id_users = (SELECT id FROM User WHERE username = '%s'), title = '%s', comment = '%s', rating = %s, submitted = CURRENT_TIMESTAMP, isEdited = TRUE WHERE id_dataProducts = (SELECT id FROM DataProducts WHERE shortKey = '%s') AND id_users = (SELECT id FROM User WHERE username = '%s') AND isDeleted = FALSE".formatted(dataProductRating.getShortKey(), dataProductRating.getUserName(), dataProductRating.getTitle(), dataProductRating.getComment(), dataProductRating.getRating(), dataProductRating.getShortKey(), dataProductRating.getUserName());
        myJdbcTemplate.update(dataProductsSql);
    }

    public void markAsDeletedDataProductRating(String shortKey, String userName) {
        String dataProductsSql = "UPDATE DataProduct_Ratings SET isDeleted = TRUE WHERE id_dataProducts = (SELECT id FROM DataProducts WHERE shortKey = '%s') AND id_users = (SELECT id FROM User WHERE username = '%s')".formatted(shortKey, userName);
        myJdbcTemplate.update(dataProductsSql);
    }
}
