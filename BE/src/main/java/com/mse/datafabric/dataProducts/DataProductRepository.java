package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.*;
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
import java.util.List;

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
    public void insertGoogleMapsData(GoogleMapsAddressDTO[] dto){
        final String INSERT = "INSERT INTO GOOGLE_MAPS_DATA (placeId, locationLat, locationLng, postalCode) VALUES(?,?,?,?) RETURNING dataId";
        for(int i = 0;i < dto.length; i++) {
            try {
                int finalI = i;
                int query = jdbcTemplate.query(INSERT, ps -> {
                    ps.setString(1, dto[finalI].placeId);
                    ps.setDouble(2, dto[finalI].locationLat);
                    ps.setDouble(3, dto[finalI].locationLng);
                    ps.setString(4, dto[finalI].postalCode);
                }, resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                    return -1;
                });
                dto[finalI].dataId = query;
            } catch (Exception e) {
                return;
            }
        }
    }
    public int updateDataProductGoogleMapsIds(GoogleMapsAddressDTO[] dto, DataProductSQLWhitelists[] whitelist) {
        if(whitelist.length!=3)
            return 0;
        int count = 0;
        String[] selectColumn = whitelist[0].selectColumn.split("\\.");
        final String UPDATE_ID = "UPDATE "+whitelist[0].tableName+" SET "+selectColumn[selectColumn.length-1]+" = ? "+
                "WHERE "+whitelist[1].selectColumn+" = ? AND "+whitelist[2].selectColumn+" = ?";
        for(int i = 0;i < dto.length; i++){
            try {
                int finalI = i;
                if(dto[finalI].dataId == -1)
                    continue;
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_ID);
                    ps.setInt(1, dto[finalI].dataId);
                    ps.setString(2, dto[finalI].city);
                    ps.setString(3, dto[finalI].street);
                    return ps;
                });
                count++;
            }
            catch (Exception e) {
                return 0;
            }
        }
        return count;
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
    private String[] getQueryResultList(String sqlQuery, PreparedStatementSetter ps){
        try {
            return jdbcTemplate.query(sqlQuery, ps,
                    resultSet -> {
                        List<String> results = new ArrayList<>();
                        while (resultSet.next()) {
                            results.add(resultSet.getString(1));
                        }
                        return results.toArray(new String[0]);
                    }
            );
        }
        catch (Exception e) {
            return null;
        }
    }

    private String getMapsJoin(String shortkey){
        if(!connectedToMaps(shortkey))
            return null;
        return " JOIN google_maps_data ON google_maps_data.dataid = " + shortkey + ".googlemapsdataid";
    }

    private boolean connectedToMaps(String shortkey){
        final String STATEMENT = "SELECT COUNT (*) FROM dataproduct_maps_data JOIN dataproducts ON dataproducts.id = dataproduct_maps_data.dataproduct_id WHERE dataproducts.shortkey = ?";
        Integer count = jdbcTemplate.query(
                STATEMENT, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setString(1, shortkey);
                    }
                }, (ResultSetExtractor<Integer>) resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                    return 0;
                }
        );
        return count != null && count > 0;
    }
    public boolean shortkeyIsTableName(String shortkey){
        final String STATEMENT = "SELECT COUNT(table_name) FROM information_schema.tables" +
                " WHERE  table_schema LIKE 'public' AND table_type LIKE 'BASE TABLE' AND table_name = ?";
        Integer count = jdbcTemplate.query(
                STATEMENT, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setString(1, shortkey);
                    }
                }, (ResultSetExtractor<Integer>) resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                    return 0;
                }
        );
        return count != null && count > 0;
    }
}
