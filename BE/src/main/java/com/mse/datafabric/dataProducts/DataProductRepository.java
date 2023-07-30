package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductSQLFilterDTO;
import com.mse.datafabric.dataProducts.models.DataProductSQLWhitelists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public class DataProductRepository {
    public static final Logger LOGGER= LoggerFactory.getLogger(DataProductRepository.class);
    public final JdbcTemplate jdbcTemplate;
    public DataProductRepository() {
        jdbcTemplate = new JdbcTemplate(getDataSource());
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
    public String[] getDifferentColumnValues(DataProductSQLWhitelists whitelist) {
        final String SQL = "SELECT DISTINCT " + whitelist.selectColumn + " FROM " + whitelist.tableName;
        try {
            return jdbcTemplate.queryForList(SQL, String.class).toArray(new String[0]);
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
            return query;
        }
        catch (Exception e) {
            return 0;
        }
    }
    private PreparedStatementSetter setPreparedStatementFromColumns(DataProductSQLFilterDTO filterColumnKeys, DataProductSQLFilterDTO filterColumnValues, int occurrenceCount){
        return  new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws
                    SQLException {
                int count = 1;
                for (int j = 0; j < occurrenceCount; j++) {
                    for (int i = 0; i < filterColumnKeys.getFilterValues().length; i++) {
                        if (filterColumnValues.getFilterValues()[i] != null) {
                            ps.setString(count, filterColumnValues.getFilterValues()[i]);
                            count++;
                        }
                    }
                }
            }
        };
    }
    public float getInsightAverage(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO filterValues) {
        String STATEMENT = "SELECT AVG("+whitelist.selectColumn+") FROM "+whitelist.tableName + whitelist.filter.getFilterFromColumns(filterValues);
        if(whitelist.filterExpression != null)
            if(STATEMENT.contains("WHERE"))
                STATEMENT += " AND "+whitelist.filterExpression;
            else
                STATEMENT += " WHERE "+whitelist.filterExpression;
        //
        return getQueryResult(STATEMENT, setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightCount(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO filterValues) {
        String STATEMENT = "SELECT COUNT(*) FROM "+whitelist.tableName + whitelist.filter.getFilterFromColumns(filterValues);
        if(whitelist.filterExpression != null)
            if(STATEMENT.contains("WHERE"))
                STATEMENT += " AND "+whitelist.filterExpression;
            else
                STATEMENT += " WHERE "+whitelist.filterExpression;
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightHighest(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO filterValues) {
        String STATEMENT = "SELECT MAX("+whitelist.selectColumn+") FROM "+whitelist.tableName+ whitelist.filter.getFilterFromColumns(filterValues);
        if(whitelist.filterExpression != null)
            if(STATEMENT.contains("WHERE"))
                STATEMENT += " AND "+whitelist.filterExpression;
            else
                STATEMENT += " WHERE "+whitelist.filterExpression;
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightLowest(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO filterValues) {
        String STATEMENT = "SELECT MIN("+whitelist.selectColumn+") FROM "+whitelist.tableName + whitelist.filter.getFilterFromColumns(filterValues) ;
        if(whitelist.filterExpression != null)
            if(STATEMENT.contains("WHERE"))
                STATEMENT += " AND "+whitelist.filterExpression;
            else
                STATEMENT += " WHERE "+whitelist.filterExpression;
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,1));
    }
    public float getInsightMedian(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO filterValues) {
        final String STATEMENT = "SELECT (" +
                "(SELECT MAX("+whitelist.selectColumn+") FROM "+
                "(SELECT top 50 percent "+whitelist.selectColumn+" FROM "+whitelist.tableName+ whitelist.filter.getFilterFromColumns(filterValues)+" ORDER BY "+whitelist.selectColumn+") AS onehalf)"+
                "+" +
                "(SELECT MIN("+whitelist.selectColumn+") FROM "+
                "(SELECT top 50 percent "+whitelist.selectColumn+" FROM "+whitelist.tableName+ whitelist.filter.getFilterFromColumns(filterValues)+" ORDER BY "+whitelist.selectColumn+" DESC) AS otherhalf))"+
                "/ 2 AS median";
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,2));
    }
    public float getInsightQuartile25(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO filterValues) {
        final String STATEMENT = "SELECT (" +
                "(SELECT MAX("+whitelist.selectColumn+") FROM "+
                "(SELECT top 25 percent "+whitelist.selectColumn+" FROM "+whitelist.tableName+ whitelist.filter.getFilterFromColumns(filterValues)+" ORDER BY "+whitelist.selectColumn+") AS onehalf)"+
                "+" +
                "(SELECT MIN("+whitelist.selectColumn+") FROM "+
                "(SELECT top 25 percent "+whitelist.selectColumn+" FROM "+whitelist.tableName+ whitelist.filter.getFilterFromColumns(filterValues)+" ORDER BY "+whitelist.selectColumn+" DESC) AS otherhalf))"+
                "/ 2 AS quartile";
        //
        return getQueryResult(STATEMENT,setPreparedStatementFromColumns(whitelist.filter, filterValues,2));
    }
    public float getInsightQuartile75(DataProductSQLWhitelists whitelist, DataProductSQLFilterDTO filterValues) {
        final String STATEMENT = "SELECT (" +
                "(SELECT MAX("+whitelist.selectColumn+") FROM "+
                "(SELECT top 75 percent "+whitelist.selectColumn+" FROM "+whitelist.tableName+ whitelist.filter.getFilterFromColumns(filterValues)+" ORDER BY "+whitelist.selectColumn+") AS onehalf)"+
                "+" +
                "(SELECT MIN("+whitelist.selectColumn+") FROM "+
                "(SELECT top 75 percent "+whitelist.selectColumn+" FROM "+whitelist.tableName+ whitelist.filter.getFilterFromColumns(filterValues)+" ORDER BY "+whitelist.selectColumn+" DESC) AS otherhalf))"+
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
