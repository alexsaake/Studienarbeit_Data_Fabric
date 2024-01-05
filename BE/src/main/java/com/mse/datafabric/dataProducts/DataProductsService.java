package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.payload.RatingDetailsDTO;
import com.mse.datafabric.dataProducts.payload.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static org.hibernate.grammars.hql.HqlParser.CURRENT_TIMESTAMP;

@ShellComponent
@Component
class DataProductsService implements IDataProductsService
{
    JdbcTemplate myJdbcTemplate;
    // Root location where the images should be saved
    private final Path rootLocation = Paths.get("BE/src/main/resources");
    @Autowired
    public DataProductsService(JdbcTemplate jdbcTemplate) {myJdbcTemplate = jdbcTemplate;}

    public List<DataProductOverviewResponse> getDataProductsOverview()
    {
        String dataProductsSql = "SELECT dp.id, dp.title, usr.username, dp.lastUpdated, dp.categoryId, AVG(rate.rating)::REAL averageRating FROM DataProducts dp JOIN Users usr ON dp.userid = usr.id JOIN dataproduct_ratings rate ON dp.id = rate.id_dataproducts WHERE dp.isDeleted = FALSE AND rate.isDeleted = FALSE GROUP BY dp.id, dp.title, usr.username, dp.lastUpdated, dp.categoryId";
        List<Map<String, Object>> databaseDataProducts = myJdbcTemplate.queryForList(dataProductsSql);

        List<DataProductOverviewResponse> dataProducts = new ArrayList<>();

        for (Map databaseDataProduct : databaseDataProducts)
        {
            DataProductOverviewResponse dataProduct = new DataProductOverviewResponse(
                (Long) databaseDataProduct.get("id"),
                (String) databaseDataProduct.get("title"),
                (String) databaseDataProduct.get("username"),
                new Date(((Timestamp) databaseDataProduct.get("lastUpdated")).getTime()),
                (Long) databaseDataProduct.get("categoryId"),
                (Float) databaseDataProduct.get("averageRating")
            );
            dataProducts.add(dataProduct);
        }

        return dataProducts;
    }
    public String saveDataProductImage(long dataProductId, MultipartFile image, JdbcTemplate myJdbcTemplate) throws Exception {
        // Generate a unique filename for the image, preserving the file extension
        String originalFilename = image.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + fileExtension;

        byte[] imageData;
        try {
            // Convert the MultipartFile into a byte array
            imageData = image.getBytes();
        } catch (IOException e) {
            throw new Exception("Error while converting image to byte array", e);
        }

        // Insert the image data into the image_table
        String insertImageSql = "INSERT INTO image_table (dataProductId, imageData) VALUES (?, ?)" +
                                "ON CONFLICT (dataProductId) DO UPDATE SET imageData = EXCLUDED.imageData";
            myJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertImageSql);
            ps.setLong(1, dataProductId);
            ps.setBytes(2, imageData);
            return ps;
        });

        // Update the DataProduct record with the image filename
        String updateSql = "UPDATE DataProducts SET imageFileName = ? WHERE id = ?";
        myJdbcTemplate.update(updateSql, filename, dataProductId);

        // Return the generated filename
        return filename;
    }

    public byte[] getDataProductImageData(long dataProductId) throws SQLException {
        // SQL query to fetch the image data
        String sql = "SELECT imageData FROM image_table WHERE dataProductId = ?";
        return myJdbcTemplate.queryForObject(sql, new Object[]{dataProductId}, (rs, rowNum) -> rs.getBytes("imageData"));
    }
    public DataProductSummaryResponse getDataProductSummary(long dataProductId) {
        String dataProductSql = "SELECT imageFileName, shortDescription, accessRightId FROM DataProducts WHERE id = '%s' AND isDeleted = FALSE".formatted(dataProductId);
        Map<String, Object> databaseDataProduct = myJdbcTemplate.queryForMap(dataProductSql);

        return new DataProductSummaryResponse(
                (String) databaseDataProduct.get("imageFileName"),
                (String) databaseDataProduct.get("shortDescription"),
                (Long) databaseDataProduct.get("accessRightId")
        );
    }

    public DataProductDetailsReponse getDataProductDetails(long dataProductId) {
        String dataProductSql = "SELECT description, source, sourceLink, createdon FROM DataProducts WHERE id = '%s' AND isDeleted = FALSE".formatted(dataProductId);
        Map<String, Object> databaseDataProduct = myJdbcTemplate.queryForMap(dataProductSql);

        return new DataProductDetailsReponse(
            (String)databaseDataProduct.get("description"),
            (String)databaseDataProduct.get("source"),
            (String)databaseDataProduct.get("sourceLink"),
                (java.sql.Date) databaseDataProduct.get("createdon")
        );
    }

    public boolean softDeleteDataProduct(String userName, long dataProductId) {
        String SOFT_DELETE_DATA_PRODUCT = "UPDATE DataProducts SET isDeleted = TRUE WHERE id = ? AND userid = (SELECT id FROM Users WHERE userName = ?)";
        return myJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SOFT_DELETE_DATA_PRODUCT);
            ps.setLong(1, dataProductId);
            ps.setString(2, userName);
            return ps;
        }) > 0;
    }

    public List<RatingReponse> getDataProductRatings(long dataProductId)
    {
        String ratingSql = "SELECT rate.id, rate.id_dataProducts FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id WHERE dp.id = '%s' AND rate.isDeleted = FALSE AND dp.isDeleted = FALSE".formatted(dataProductId);
        List<Map<String, Object>> databaseRatings = myJdbcTemplate.queryForList(ratingSql);

        List<RatingReponse> ratings = new ArrayList<>();

        for (Map databaseRating : databaseRatings)
        {
            RatingReponse dataProductRating = new RatingReponse(
                    (Long) databaseRating.get("id"),
                    (Long) databaseRating.get("id_dataProducts")
            );
            ratings.add(dataProductRating);
        }

        return ratings;
    }

    public RatingDetailsDTO getDataProductRatingDetails(long ratingId)
    {
        String ratingSql = "SELECT usr.userName, rate.title, rate.comment, rate.rating, rate.submitted, rate.isEdited FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN Users usr ON rate.id_users = usr.id WHERE rate.id = '%s' AND rate.isDeleted = FALSE AND dp.isDeleted = FALSE".formatted(ratingId);
        Map<String, Object> databaseRating = myJdbcTemplate.queryForMap(ratingSql);

        return new RatingDetailsDTO(
                (String)databaseRating.get("userName"),
                (String)databaseRating.get("title"),
                (String)databaseRating.get("comment"),
                ((BigDecimal)databaseRating.get("rating")).intValue(),
                new Date(((Timestamp) databaseRating.get("submitted")).getTime()),
                (Boolean)databaseRating.get("isEdited")
        );
    }

    public RatingMaxLengthsResponse getDataProductRatingMaxLengths()
    {
        String dataProductsSql = "SELECT title, comment FROM DataProduct_Ratings";
        SqlRowSet rowSet = myJdbcTemplate.queryForRowSet(dataProductsSql);
        SqlRowSetMetaData metaData = rowSet.getMetaData();
        return new RatingMaxLengthsResponse(metaData.getPrecision(1), metaData.getPrecision(2));
    }

    public boolean getDataProductRatingCanSubmit(String userName, long dataProductId)
    {
        String dataProductsSql = "SELECT * FROM DataProduct_Ratings rate JOIN DataProducts dp ON rate.id_dataProducts = dp.id JOIN Users usr ON rate.id_users = usr.id WHERE dp.id = '%s' AND usr.userName = '%s' AND rate.isDeleted = FALSE and dp.isDeleted = FALSE".formatted(dataProductId, userName);
        List<Map<String, Object>> databaseDataProductsRating = myJdbcTemplate.queryForList(dataProductsSql);

        return databaseDataProductsRating.size() == 0;
    }

    public boolean setDataProductsRating(String userName, long dataProductId, RatingDetailsDTO ratingDetails) {
        String dataProductsSql = "INSERT INTO DataProduct_Ratings (id_users, id_dataProducts, title, comment, rating) VALUES ((SELECT id FROM users WHERE userName = '%s'), '%s', '%s', '%s', %s)".formatted(userName, dataProductId, ratingDetails.getTitle(), ratingDetails.getComment(), ratingDetails.getRating());
        return myJdbcTemplate.update(dataProductsSql) > 0;
    }

    public boolean updateDataProductsRating(String userName, long ratingId, RatingDetailsDTO ratingDetails) {
        String dataProductsSql = "UPDATE DataProduct_Ratings SET title = '%s', comment = '%s', rating = %s, submitted = CURRENT_TIMESTAMP, isEdited = TRUE WHERE id = '%s' AND id_users = (SELECT id FROM Users WHERE userName = '%s') AND isDeleted = FALSE".formatted(ratingDetails.getTitle(), ratingDetails.getComment(), ratingDetails.getRating(), ratingId, userName);
        return myJdbcTemplate.update(dataProductsSql) > 0;
    }

    public boolean markAsDeletedDataProductRating(String userName, long ratingId) {
        String dataProductsSql = "UPDATE DataProduct_Ratings SET isDeleted = TRUE WHERE id = '%s' AND id_users = (SELECT id FROM Users WHERE userName = '%s') AND isDeleted = FALSE".formatted(ratingId, userName);
        return myJdbcTemplate.update(dataProductsSql) > 0;
    }
}
