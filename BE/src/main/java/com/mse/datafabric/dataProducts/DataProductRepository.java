package com.mse.datafabric.dataProducts;

import com.mse.datafabric.utils.GoogleMapsAPI;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.*;

import static jakarta.xml.bind.DatatypeConverter.parseFloat;

@Repository
public class DataProductRepository {
    public static final Logger LOGGER= LoggerFactory.getLogger(DataProductRepository.class);
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;

    public DataProductRepository() {

    }

    public void updateDataProductDate(String shortKey, Date date) {
        final String UPDATE_DATE_DATA_PRODUCT = "UPDATE DATAPRODUCTS SET LASTUPDATED = ? WHERE SHORTKEY = ?";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(UPDATE_DATE_DATA_PRODUCT);
                ps.setDate(1, date);
                ps.setString(2, shortKey);
                return ps;
            });
        }
        catch (Exception e) {

        }
    }
    public GoogleMapsAddressDTO getAddressColumns(String shortkey){
        final String STATEMENT = "SELECT maps_city_column, maps_street_column FROM dataproduct_maps_data " +
                "JOIN dataproducts ON dataproducts.id = dataproduct_maps_data.dataproduct_id WHERE dataproducts.shortkey = ?";
        try {
            return jdbcTemplate.query(
                    STATEMENT, preparedStatement ->
                            preparedStatement.setString(1, shortkey),
                    (ResultSetExtractor<GoogleMapsAddressDTO>) resultSet -> {
                        if (resultSet.next())
                            return new GoogleMapsAddressDTO(resultSet.getString(1),resultSet.getString(2));
                        return null;
                    }
            );
        }
        catch (Exception e) {
            return null;
        }
    }
    public Float getAvgRatings(String shortKey){

        final String queryAvg = "SELECT AVG(rating) FROM dataproduct_ratings JOIN dataproducts ON dataproducts.id = dataproduct_ratings.id_dataproducts WHERE dataproducts.shortkey = ?";

        try {
            return parseFloat(jdbcTemplate.queryForObject(queryAvg, new Object[]{shortKey}, String.class));
        }

        catch (Exception e) {
            return 0.0F;
        }
    }
    public String getData(String shortkey){
        final String STATEMENT = "SELECT data FROM dataproducts WHERE dataproducts.shortkey = ?";
        try {
            return jdbcTemplate.query(
                STATEMENT, preparedStatement ->
                        preparedStatement.setString(1, shortkey),
                (ResultSetExtractor<String>) resultSet -> {
                    if (resultSet.next())
                        return resultSet.getString(1);
                    return null;
                }
            );
        }
        catch (Exception e) {
            return null;
        }
    }
    public void setData(String shortkey, String data){
        final String STATEMENT = "UPDATE dataproducts SET data = cast(? as jsonb) WHERE shortkey = ?";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(STATEMENT);
                ps.setString(1, data);
                ps.setString(2, shortkey);
                return ps;
            });
        }
        catch (Exception e) {
            throw e;
        }
    }
}
