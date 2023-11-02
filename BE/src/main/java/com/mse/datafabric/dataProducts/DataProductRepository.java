package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductDTO;
import com.mse.datafabric.utils.GoogleMapsAPI;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
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

import static jakarta.xml.bind.DatatypeConverter.parseFloat;

@Repository
public class DataProductRepository {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;

    public DataProductRepository() {

    }

    public void updateDataProductDate(long id, Date date) {
        final String UPDATE_DATE_DATA_PRODUCT = "UPDATE DATAPRODUCTS SET LASTUPDATED = ? WHERE id = ?";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(UPDATE_DATE_DATA_PRODUCT);
                ps.setDate(1, date);
                ps.setLong(2, id);
                return ps;
            });
        }
        catch (Exception e) {

        }
    }
    public long insertDataProduct(DataProductDTO dto){
        final String STATEMENT = "INSERT INTO DATAPRODUCTS ( title, shortdescription, description, source, sourceLink, categoryid, accessrightid, data, userid) VALUES (?, ?, ?, ?, ?, ?, ?, cast(? as jsonb), (SELECT id FROM users WHERE username = ?)) RETURNING id";
        Long id;
        try {
            id = jdbcTemplate.query(
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
                        }
                    },new ResultSetExtractor<>() {
                        public Long extractData(ResultSet rs) throws SQLException,
                                DataAccessException {
                            if (rs.next()) {
                                return rs.getLong("id");
                            }
                            return null;
                        }
                    }
            );
            if(id == null)
                return -1;
        }
        catch (Exception e) {
            return -1;
        }
        return id;
    }
    public void updateDataProduct(DataProductDTO dto, long id){
        final String STATEMENT = "UPDATE dataproducts SET title = ?, shortdescription = ?, description = ?, source = ?, sourceLink = ?, categoryid = ?, accessrightid = ?, data = cast(? as jsonb), userid = (SELECT id FROM users WHERE username = ?) WHERE id = ?";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(STATEMENT);
                ps.setString(1, dto.title);
                ps.setString(2, dto.shortDescription);
                ps.setString(3, dto.description);
                ps.setString(4, dto.source);
                ps.setString(5, dto.sourceLink);
                ps.setInt(6, dto.categoryId);
                ps.setInt(7, dto.accessRightId);
                ps.setString(8, dto.data);
                ps.setString(9, dto.username);
                ps.setLong(10, id);
                return ps;
            });
        }
        catch (Exception e){
            return;
        }
    }
    public DataProductDTO getDataProduct(long id){
        final String STATEMENT = "SELECT title, shortdescription, description, source, sourceLink, accessrightid, categoryid, data, users.username FROM dataproducts JOIN users ON users.id = dataproducts.userid WHERE id = ?";
        try {
            return jdbcTemplate.query(
                    STATEMENT, new PreparedStatementSetter() {
                        public void setValues(PreparedStatement ps) throws SQLException {
                            ps.setLong(1, id);
                        }
                    },new ResultSetExtractor<>() {
                        public DataProductDTO extractData(ResultSet rs) throws SQLException,
                                DataAccessException {
                            if (rs.next()) {
                                return new DataProductDTO(
                                        id,
                                        rs.getString(1),
                                        rs.getString(2),
                                        rs.getString(3),
                                        rs.getString(4),
                                        rs.getString(5),
                                        rs.getInt(6),
                                        rs.getInt(7),
                                        rs.getString(8),
                                        rs.getString(9)
                                );
                            }
                            return null;
                        }
                    }
            );
        }
        catch (Exception e) {
            return null;
        }
    }
    public boolean setAddressColumns(long id, GoogleMapsAddressDTO dto) {
        final String STATEMENT = "INSERT INTO dataproduct_maps_data (maps_city_column, maps_street_column, dataproduct_id) VALUES (?, ?, (SELECT id FROM dataproducts WHERE id = ?))";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(STATEMENT);
                ps.setString(1, dto.city);
                ps.setString(2, dto.street);
                ps.setLong(3, id);
                return ps;
            });
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public boolean deleteAddressColumns(long id) {
        final String STATEMENT = "DELETE FROM dataproduct_maps_data USING dataproducts WHERE dataproducts.id = dataproduct_maps_data.dataproduct_id AND dataproducts.id = ?";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(STATEMENT);
                ps.setLong(1, id);
                return ps;
            });
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public GoogleMapsAddressDTO getMapsData(long id){
        final String STATEMENT = "SELECT maps_city_column, maps_street_column, dataproduct_id FROM dataproduct_maps_data JOIN dataproducts ON dataproduct_maps_data.dataproduct_id = dataproducts.id WHERE dataproducts.id = ?";
        try {
            return jdbcTemplate.query(
                    STATEMENT, new PreparedStatementSetter() {
                        public void setValues(PreparedStatement ps) throws SQLException {
                            ps.setLong(1, id);
                        }
                    },new ResultSetExtractor<>() {
                        public GoogleMapsAddressDTO extractData(ResultSet rs) throws SQLException,
                                DataAccessException {
                            if (rs.next()) {
                                return new GoogleMapsAddressDTO(
                                        rs.getString(1),
                                        rs.getString(2)
                                );
                            }
                            return null;
                        }
                    }
            );
        }
        catch (Exception e) {
            return null;
        }
    }
    public GoogleMapsAddressDTO getAddressColumns(long id){
        final String STATEMENT = "SELECT maps_city_column, maps_street_column FROM dataproduct_maps_data " +
                "JOIN dataproducts ON dataproducts.id = dataproduct_maps_data.dataproduct_id WHERE dataproducts.id = ?";
        try {
            return jdbcTemplate.query(
                    STATEMENT, preparedStatement ->
                            preparedStatement.setLong(1, id),
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
    public Float getAvgRatings(long id){
        final String queryAvg = "SELECT AVG(rating) FROM dataproduct_ratings JOIN dataproducts ON dataproducts.id = dataproduct_ratings.id_dataproducts WHERE dataproducts.id = ?";

        try {
            return parseFloat(jdbcTemplate.queryForObject(queryAvg, new Object[]{id}, String.class));
        }

        catch (Exception e) {
            return 0.0F;
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
    public String getData(long id){
        final String STATEMENT = "SELECT data FROM dataproducts WHERE dataproducts.id = ?";
        try {
            return jdbcTemplate.query(
                STATEMENT, preparedStatement ->
                        preparedStatement.setLong(1, id),
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
    public void setData(long id, String data){
        final String STATEMENT = "UPDATE dataproducts SET data = cast(? as jsonb) WHERE id = ?";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(STATEMENT);
                ps.setString(1, data);
                ps.setLong(2, id);
                return ps;
            });
        }
        catch (Exception e) {
            throw e;
        }
    }
}
