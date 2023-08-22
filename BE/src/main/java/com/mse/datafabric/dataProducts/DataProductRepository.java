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
    public final JdbcTemplate jdbcTemplate;
    public DataProductRepository() {
        jdbcTemplate = new JdbcTemplate(getDataSource());
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
        final String INSERT = "SELECT dataId FROM FINAL TABLE (INSERT INTO GOOGLE_MAPS_DATA (placeId, locationLat, locationLng, postalCode) VALUES(?,?,?,?))";
        for(int i = 0;i < dto.length; i++) {
            try {
                int finalI = i;
                int query = jdbcTemplate.query(INSERT, ps -> {
                    ps.setString(1, dto[finalI].placeId);
                    ps.setFloat(2, dto[finalI].locationLat);
                    ps.setFloat(3, dto[finalI].locationLng);
                    ps.setString(4, dto[finalI].postalCode);
                }, resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                    return -1;
                });
                dto[finalI].dataId = query;
            } catch (Exception e) {

            }
        }
    }
    public int updateDataProductGoogleMapsIds(GoogleMapsAddressDTO[] dto, DataProductSQLWhitelists[] whitelist) {
        if(whitelist.length!=3)
            return 0;
        int count = 0;
        final String UPDATE_ID = "UPDATE "+whitelist[0].tableName+" SET "+whitelist[0].selectColumn+" = ? "+
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

            }
        }
        return count;
    }
    public GoogleMapsAddressDTO[] getDataProductAddressData(DataProductSQLWhitelists[] whitelists){
        if(whitelists.length != 2)
            return null;

        List<GoogleMapsAddressDTO> dtoLlist = new ArrayList<>();

        String query = "SELECT " + whitelists[0].selectColumn + ", "+whitelists[1].selectColumn + " FROM " + whitelists[0].tableName+
                " WHERE googleMapsDataId is NULL";
        jdbcTemplate.query(
            query, new PreparedStatementSetter() {
                public void setValues(PreparedStatement preparedStatement) throws
                        SQLException {
                }
            },new ResultSetExtractor<>() {
                public Object extractData(ResultSet resultSet) throws SQLException,
                        DataAccessException {
                    while (resultSet.next()) {

                        String city = resultSet.getString(whitelists[0].selectColumn);
                        String street = resultSet.getString(whitelists[1].selectColumn);

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
            if(filterValues.length < i || filterValues[i].areaFilter == null)
                continue;
            String str = whitelist.filter[i].areaFilter.split("\\.")[0];
            if(whitelist.joinExpression.contains(str+" ON"))
                containsColumn = true;
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
        final String STATEMENT = "SELECT (" +
                "(SELECT MAX(col1) FROM "+
                "(SELECT top 50 percent "+whitelist.selectColumn+" AS col1 FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist)+" ORDER BY "+whitelist.selectColumn+") AS onehalf)"+
                "+" +
                "(SELECT MIN(col2) FROM "+
                "(SELECT top 50 percent "+whitelist.selectColumn+" AS col2 FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist)+" ORDER BY "+whitelist.selectColumn+" DESC) AS otherhalf))"+
                "/ 2 AS median";
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,2));
    }
    public float getInsightQuartile25(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        final String STATEMENT = "SELECT (" +
                "(SELECT MAX(col1) FROM "+
                "(SELECT top 25 percent "+whitelist.selectColumn+" AS col1 FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist)+" ORDER BY "+whitelist.selectColumn+") AS onehalf)"+
                "+" +
                "(SELECT MIN(col2) FROM "+
                "(SELECT top 25 percent "+whitelist.selectColumn+" AS col2 FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist)+" ORDER BY "+whitelist.selectColumn+" DESC) AS otherhalf))"+
                "/ 2 AS quartile";
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,2));
    }
    public float getInsightQuartile75(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO[] filterValues) {
        final String STATEMENT = "SELECT (" +
                "(SELECT MAX(col1) FROM "+
                "(SELECT top 75 percent "+whitelist.selectColumn+" AS col1 FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist)+" ORDER BY "+whitelist.selectColumn+") AS onehalf)"+
                "+" +
                "(SELECT MIN(col2) FROM "+
                "(SELECT top 75 percent "+whitelist.selectColumn+" AS col2 FROM "+whitelist.tableName+ getSQLJoin(filterValues, whitelist) + getSQLFilter(filterValues, whitelist)+" ORDER BY "+whitelist.selectColumn+" DESC) AS otherhalf))"+
                "/ 2 AS quartile";
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,2));
    }
    public static DriverManagerDataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:h2:file:./db/datafabric;NON_KEYWORDS=USER;AUTO_SERVER=true");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;

    }
}
