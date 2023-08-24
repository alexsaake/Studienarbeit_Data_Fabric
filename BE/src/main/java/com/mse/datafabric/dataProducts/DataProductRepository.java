package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductSQLFilterDTO;
import com.mse.datafabric.dataProducts.models.DataProductSQLWhitelists;
import com.mse.datafabric.utils.GoogleMapsAPI;
import com.mse.datafabric.utils.dtos.GoogleMapsAddressDTO;
import io.micrometer.common.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class DataProductRepository {
    public static final Logger LOGGER= LoggerFactory.getLogger(DataProductRepository.class);
    @Autowired
    public JdbcTemplate jdbcTemplate;
    public DataProductRepository() {

    }

    @Autowired
    public GoogleMapsAPI googleMapsAPI;

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
    public GoogleMapsAddressDTO[] getDataProductAddressData(DataProductSQLWhitelists[] whitelists){
        if(whitelists.length != 2)
            return null;

        List<GoogleMapsAddressDTO> dtoLlist = new ArrayList<>();

        String query = "SELECT " + whitelists[0].selectColumn + ", "+whitelists[1].selectColumn + " FROM " + whitelists[0].tableName+
                " WHERE googleMapsDataId is NULL";;
        jdbcTemplate.query(
            query, new PreparedStatementSetter() {
                public void setValues(PreparedStatement preparedStatement) throws SQLException {
                }
            },new ResultSetExtractor<>() {
                public Object extractData(ResultSet resultSet) throws SQLException,
                        DataAccessException {
                    while (resultSet.next()) {
                        String[] columns = whitelists[0].selectColumn.split("\\.");
                        String city = resultSet.getString(columns[columns.length-1]);
                        columns = whitelists[1].selectColumn.split("\\.");
                        String street = resultSet.getString(columns[columns.length-1]);

                        GoogleMapsAddressDTO dto = new GoogleMapsAddressDTO(city, street);
                        googleMapsAPI.updateDTO(dto);

                        dtoLlist.add(dto);
                    }
                    return null;
                }
            }
        );
        return dtoLlist.toArray(new GoogleMapsAddressDTO[0]);
    }
    public String[] getDifferentColumnValues(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        String SQL = "SELECT DISTINCT " + whitelist.selectColumn + " FROM " + whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist);
        if(whitelist.filterExpression!=null)
            if(SQL.contains("WHERE"))
                SQL += " AND "+whitelist.filterExpression;
            else
                SQL += " WHERE "+whitelist.filterExpression;
        try {
            PreparedStatementSetter ps = setPreparedStatementFromColumns(whitelist.filter, filterValues,1);
            return getQueryResultList(SQL, ps);
        }
        catch (Exception e) {
            return null;
        }
    }
    private float getQueryResult(String sqlQuery, PreparedStatementSetter ps){
        try {
            Float query = jdbcTemplate.query(sqlQuery, ps,
                    (ResultSetExtractor<Float>) resultSet -> {
                        if (resultSet.next()) {
                            return resultSet.getFloat(1);
                        }
                        return null;
                    }
            );
            if (query==null)
                return 0;
            return (float) (Math.round(query * 100.0) / 100.0);
        }
        catch (Exception e) {
            return 0;
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
    private PreparedStatementSetter setPreparedStatementFromColumns(DataProductSQLFilterDTO[] filterColumnKeys, DataProductSQLFilterDTO[] filterColumnValues, int occurrenceCount){
        return  new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws
                    SQLException {
                int count = 1;
                for (int j = 0; j < occurrenceCount; j++) {
                    for (int k = 0; k < filterColumnValues.length; k++) {
                        if(k >= filterColumnKeys.length)
                            continue;
                        for (int i = 0; i < filterColumnKeys[k].getFilterValues().length; i++) {
                            if (filterColumnValues[k].getFilterValues()[i] != null) {
                                String[] splits = filterColumnValues[k].getFilterValues()[i].split(",");
                                for(int h = 0;h<splits.length;h++){
                                    ps.setString(count, splits[h]);
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        };
    }
    public String getSQLFilter(DataProductSQLFilterDTO[] filterValues, DataProductSQLWhitelists whitelist){
        String sqlFilter = "";
        String tempFilter;
        if(whitelist.filter == null)
            return sqlFilter;
        for(int i = 0;i < filterValues.length;i++)
        {
            if(i >= whitelist.filter.length)
                continue;
            tempFilter = whitelist.filter[i].getFilterFromColumns(filterValues[i]);
            if(!tempFilter.equals(""))
            {
                if(sqlFilter.equals(""))
                    sqlFilter = " WHERE " + tempFilter;
                else
                    sqlFilter += " AND " + tempFilter;
            }
        }
        return sqlFilter;
    }
    public String getSQLJoin(DataProductSQLFilterDTO[] filterValues, DataProductSQLWhitelists whitelist){
        String sqlFilter = "";
        boolean containsColumn = false;
        if(whitelist.joinExpression == null || whitelist.filter == null)
            return sqlFilter;
        for(int i = 0;i < whitelist.filter.length;i++){
            if(filterValues.length < i ) {
               continue;
            }
            if(filterValues[i].areaFilter != null) {
                String str = whitelist.filter[i].areaFilter.split("\\.")[0];
                if (whitelist.joinExpression.contains(str + " ON"))
                    containsColumn = true;
            }
            if(filterValues[i].dateFromFilter != null) {
                String str = whitelist.filter[i].dateFromFilter.split("\\.")[0];
                if (whitelist.joinExpression.contains(str + " ON"))
                    containsColumn = true;
            }
            if(filterValues[i].dateToFilter != null) {
                String str = whitelist.filter[i].dateToFilter.split("\\.")[0];
                if (whitelist.joinExpression.contains(str + " ON"))
                    containsColumn = true;
            }
        }
        if(!containsColumn)
            return sqlFilter;
        sqlFilter=" JOIN " + whitelist.joinExpression;
        return sqlFilter;
    }
    public float getInsightAverage(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        String STATEMENT = "SELECT AVG("+whitelist.selectColumn+") FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist);
        if(whitelist.filterExpression != null)
            if(STATEMENT.contains("WHERE"))
                STATEMENT += " AND "+whitelist.filterExpression;
            else
                STATEMENT += " WHERE "+whitelist.filterExpression;
        //
        return getQueryResult(STATEMENT, setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightCount(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        String STATEMENT = "SELECT COUNT(*) FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist)  + getSQLFilter(filterValues, whitelist);
        if(whitelist.filterExpression != null)
            if(STATEMENT.contains("WHERE"))
                STATEMENT += " AND "+whitelist.filterExpression;
            else
                STATEMENT += " WHERE "+whitelist.filterExpression;
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightHighest(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        String STATEMENT = "SELECT MAX("+whitelist.selectColumn+") FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist);
        if(whitelist.filterExpression != null)
            if(STATEMENT.contains("WHERE"))
                STATEMENT += " AND "+whitelist.filterExpression;
            else
                STATEMENT += " WHERE "+whitelist.filterExpression;
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightLowest(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        String STATEMENT = "SELECT MIN("+whitelist.selectColumn+") FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist) ;
        if(whitelist.filterExpression != null)
            if(STATEMENT.contains("WHERE"))
                STATEMENT += " AND "+whitelist.filterExpression;
            else
                STATEMENT += " WHERE "+whitelist.filterExpression;
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightMedian(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        final String STATEMENT = "SELECT PERCENTILE_CONT(0.5) WITHIN GROUP(ORDER BY "+whitelist.selectColumn+") FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist);
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightQuartile25(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        final String STATEMENT = "SELECT PERCENTILE_CONT(0.25) WITHIN GROUP(ORDER BY "+whitelist.selectColumn+") FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist);
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightQuartile75(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        final String STATEMENT = "SELECT PERCENTILE_CONT(0.75) WITHIN GROUP(ORDER BY "+whitelist.selectColumn+") FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist);
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public GoogleMapsAddressDTO[] getInsightMapsData(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        final String STATEMENT = "SELECT locationlat, locationlng FROM google_maps_data "+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist);
        //
        List<GoogleMapsAddressDTO> dtoLlist = new ArrayList<>();
        //
        jdbcTemplate.query(
                STATEMENT, setPreparedStatementFromColumns(whitelist.filter, filterValues,1),new ResultSetExtractor<>() {
                    public Object extractData(ResultSet resultSet) throws SQLException,
                            DataAccessException {
                        while (resultSet.next()) {
                            dtoLlist.add(new GoogleMapsAddressDTO(resultSet.getDouble("locationlat"),resultSet.getDouble("locationlng")));
                        }
                        return null;
                    }
                }
        );
        return dtoLlist.toArray(new GoogleMapsAddressDTO[0]);
    }
}
