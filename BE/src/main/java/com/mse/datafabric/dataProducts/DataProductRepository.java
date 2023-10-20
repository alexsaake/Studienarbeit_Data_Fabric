package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductDTO;
import com.mse.datafabric.utils.GoogleMapsAPI;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String insertDataProduct(DataProductDTO dto){
        final String STATEMENT = "INSERT INTO DATAPRODUCTS ( title, shortdescription, description, source, sourceLink, categoryid, accessrightid, data, userid,shortkey) VALUES (?, ?, ?, ?, ?, ?, ?, cast(? as jsonb), (SELECT id FROM users WHERE username = ?),?) RETURNING id";
        final String STATEMENT2 = "UPDATE DATAPRODUCTS SET shortkey = ? WHERE id = ?";

        String shortKey = dto.title.replaceAll(" ","_");
        try {
            String finalShotKey1 = shortKey;
            Integer id = jdbcTemplate.query(
                    STATEMENT, new PreparedStatementSetter() {
                        public void setValues(PreparedStatement ps) throws SQLException {
                                    ps.setString(1, dto.title);
                                    ps.setString(2, dto.shortDescription);
                                    ps.setString(3, dto.description);
                                    ps.setString(4, dto.source);
                                    ps.setString(5, dto.sourceLink);
                                    ps.setInt(6, dto.categoryId);
                                    ps.setInt(7, dto.accessRightId);
                                    ps.setString(8, dto.data);
                                    ps.setString(9, dto.username);
                                    ps.setString(10, finalShotKey1);
                        }
                    },new ResultSetExtractor<>() {
                        public Integer extractData(ResultSet rs) throws SQLException,
                                DataAccessException {
                            if (rs.next()) {
                                return rs.getInt(1);
                            }
                            return null;
                        }
                    }
            );
            if(id == null)
                return null;
            shortKey = finalShotKey1 + "_" + id;
            String finalShortKey = shortKey;
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(STATEMENT2);
                ps.setString(1, finalShortKey);
                ps.setInt(2,id);
                return ps;
            });
        }
        catch (Exception e) {
            return null;
        }
        return shortKey;
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
    public String getAvgRatings(String shortKey){
        final String queryAvg = "SELECT AVG(rating) FROM dataproduct_ratings JOIN dataproducts ON dataproducts.id = dataproduct_ratings.id_dataproducts WHERE dataproducts.shortkey = ?";
        try {
            return jdbcTemplate.queryForObject(queryAvg, new Object[]{shortKey}, String.class);
        }
        catch (Exception e) {
            return null;
        }
    }
    public Map<String, String>[] getCategories(){
        final String STATEMENT = "SELECT DISTINCT dataproduct_categories.id, dataproduct_categories.category FROM dataproduct_categories JOIN dataproducts ON dataproducts.categoryid = dataproduct_categories.id";
        try {
            return jdbcTemplate.query(STATEMENT, this::getKeyValue);
        }
        catch (Exception e) {
            return null;
        }
    }
    public Map<String, String>[] getAccessRights(){
        final String STATEMENT = "SELECT DISTINCT dataproduct_accessrights.id, dataproduct_accessrights.accessright FROM dataproduct_accessrights JOIN dataproducts ON dataproducts.accessrightid = dataproduct_accessrights.id";
        try {
            return jdbcTemplate.query(STATEMENT, this::getKeyValue);
        }
        catch (Exception e) {
            return null;
        }
    }
    public Map<String, String>[] getKeyValue(ResultSet resultSet){
        try {
            List<Map<String, String>> hashList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("key",resultSet.getString(1));
                map.put("value",resultSet.getString(2));
                hashList.add(map);
            }
            return hashList.toArray(new Map[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
