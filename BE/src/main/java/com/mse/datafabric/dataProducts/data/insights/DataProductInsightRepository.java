package com.mse.datafabric.dataProducts.data.insights;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DataProductInsightRepository {
    public static final Logger LOGGER= LoggerFactory.getLogger(DataProductInsightRepository.class);
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;
    @Autowired
    private DataProductInsights insights;

    public String[] getFilterValues(String shortkey, int filterId) {
        if(!shortkeyIsTableName(shortkey))
            return null;
        return getDifferentColumnValues(shortkey, filterId);
    }
    private String[] getDifferentColumnValues(String shortkey, int filterId) {
        String SQL = "SELECT filter_column FROM insight_filters JOIN dataproducts ON dataproducts.id = insight_filters.dataproduct_id WHERE insight_filters.filter_id = ? AND dataproducts.shortkey = ?";
        try {
            PreparedStatementSetter ps = ps1 -> {
                ps1.setInt(1, filterId);
                ps1.setString(2, shortkey);
            };
            String[] column = getQueryResultList(SQL, ps);
            if(column == null || column.length == 0)
                return null;
            return getQueryResultList("SELECT DISTINCT "+ column[0] +" FROM " + shortkey +getMapsJoin(shortkey),null);
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
    private String getMapsJoin(String shortkey){
        if(!connectedToMaps(shortkey))
            return null;
        return " JOIN google_maps_data ON google_maps_data.dataid = " + shortkey + ".googlemapsdataid";
    }
    public DataProductInsightDataDTO[] getInsights(String shortkey){
        if(!shortkeyIsTableName(shortkey))
            return null;
        List<DataProductInsightDataDTO> dtoList = new ArrayList<>();
        String query = "SELECT * FROM dataproduct_insights JOIN dataproducts ON dataproducts.id = dataproduct_insights.dataproduct_id WHERE dataproducts.shortkey = ?";
        jdbcTemplate.query(
                query, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setString(1, shortkey);
                    }
                },new ResultSetExtractor<>() {
                    public Object extractData(ResultSet resultSet) throws SQLException,
                            DataAccessException {
                        while (resultSet.next()) {
                            int type = resultSet.getInt("type");
                            String displayName = resultSet.getString("display_name");
                            String unit = resultSet.getString("unit");
                            String column = resultSet.getString("dataproduct_column");

                            DataProductInsightDataDTO dto = new DataProductInsightDataDTO(getInsightValue(type,shortkey, column),displayName, unit);

                            dtoList.add(dto);
                        }
                        return null;
                    }
                }
        );
        return dtoList.toArray(new DataProductInsightDataDTO[0]);
    }
    public InsightFilterDTO[] getInsightFilters(String shortkey){
        if(!shortkeyIsTableName(shortkey))
            return null;
        List<InsightFilterDTO> dtoList = new ArrayList<>();
        String query = "SELECT * FROM insight_filters JOIN dataproducts ON dataproducts.id = insight_filters.dataproduct_id WHERE dataproducts.shortkey = ? ORDER BY insight_filters.type_id DESC";
        jdbcTemplate.query(
                query, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setString(1, shortkey);
                    }
                },new ResultSetExtractor<>() {
                    public Object extractData(ResultSet resultSet) throws SQLException,
                            DataAccessException {
                        while (resultSet.next()) {
                            int type = resultSet.getInt("type_id");
                            int filterId = resultSet.getInt("filter_id");
                            String displayName = resultSet.getString("display_name");

                            InsightFilterDTO dto = new InsightFilterDTO(displayName, type, filterId);

                            dtoList.add(dto);
                        }
                        return null;
                    }
                }
        );
        return dtoList.toArray(new InsightFilterDTO[0]);
    }
    private float getInsightValue(int type, String shortkey, String columnName){
        switch (type){
            case 1:
                return insights.getCount(columnName);
            case 2:
                return insights.getAverage(columnName);
            case 3:
                return 0;//getInsightHighest(shortkey, columnName, filter);
            case 4:
                return 0;//getInsightLowest(shortkey, columnName, filter);
            case 5:
                return 0;//getInsightMedian(shortkey, columnName, filter);
            case 6:
                return 0;//getInsightQuartile25(shortkey, columnName, filter);
            case 7:
                return 0;//getInsightQuartile75(shortkey, columnName, filter);
        }
        return 0;
    }

    private float getInsightCount(String shortkey, String columnName, DataProductInsightFilter filter) {
        String STATEMENT = "SELECT COUNT("+ columnName +") FROM " + shortkey + getMapsJoin(shortkey);
        STATEMENT += filter.getSQLFilter(shortkey,STATEMENT);
        //
        return getQueryResult(STATEMENT,null);
    }

    private float getInsightAverage(String shortkey, String columnName, DataProductInsightFilter filter) {
        String STATEMENT = "SELECT AVG("+columnName+") FROM " + shortkey + getMapsJoin(shortkey);
        STATEMENT += filter.getSQLFilter(shortkey,STATEMENT);
        //
        return getQueryResult(STATEMENT, null);
    }
    private float getInsightHighest(String shortkey, String columnName, DataProductInsightFilter filter) {
        String STATEMENT = "SELECT MAX("+columnName+") FROM " + shortkey + getMapsJoin(shortkey);
        STATEMENT += filter.getSQLFilter(shortkey,STATEMENT);
        //
        return getQueryResult(STATEMENT,null);
    }
    private float getInsightLowest(String shortkey, String columnName, DataProductInsightFilter filter) {
        String STATEMENT = "SELECT MIN("+columnName+") FROM " + shortkey + getMapsJoin(shortkey);
        STATEMENT += filter.getSQLFilter(shortkey,STATEMENT);
        //
        return getQueryResult(STATEMENT,null);
    }
    private float getInsightMedian(String shortkey, String columnName, DataProductInsightFilter filter) {
        String STATEMENT = "SELECT PERCENTILE_CONT(0.5) WITHIN GROUP(ORDER BY "+columnName+") FROM " + shortkey + getMapsJoin(shortkey);
        STATEMENT += filter.getSQLFilter(shortkey,STATEMENT);
        //
        return getQueryResult(STATEMENT,null);
    }
    private float getInsightQuartile25(String shortkey, String columnName, DataProductInsightFilter filter) {
        String STATEMENT = "SELECT PERCENTILE_CONT(0.25) WITHIN GROUP(ORDER BY "+columnName+") FROM " + shortkey + getMapsJoin(shortkey);
        STATEMENT += filter.getSQLFilter(shortkey,STATEMENT);
        //
        return getQueryResult(STATEMENT,null);
    }
    private float getInsightQuartile75(String shortkey, String columnName, DataProductInsightFilter filter) {
        String STATEMENT = "SELECT PERCENTILE_CONT(0.75) WITHIN GROUP(ORDER BY "+columnName+") FROM " + shortkey + getMapsJoin(shortkey);
        STATEMENT += filter.getSQLFilter(shortkey,STATEMENT);
        //
        return getQueryResult(STATEMENT,null);
    }
    public GoogleMapsAddressDTO[] getInsightMapsData(String shortkey, DataProductInsightFilter filter) {
        if(!connectedToMaps(shortkey))
            return null;
        String STATEMENT = "SELECT locationlat, locationlng FROM google_maps_data JOIN "+ shortkey + " ON google_maps_data.dataid = " + shortkey + ".googlemapsdataid";
        STATEMENT += filter.getSQLFilter(shortkey,STATEMENT);
        //
        List<GoogleMapsAddressDTO> dtoLlist = new ArrayList<>();
        //
        jdbcTemplate.query(
                STATEMENT, new ResultSetExtractor<>() {
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
    public String getDataProductData(String shortkey) {
        String STATEMENT = "SELECT data FROM dataproducts WHERE shortkey = ?";
        //
        return jdbcTemplate.query(
                STATEMENT, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setString(1, shortkey);
                    }
                }, new ResultSetExtractor<>() {
                    public String extractData(ResultSet resultSet) throws SQLException,
                            DataAccessException {
                        if (resultSet.next()) {
                            return resultSet.getString(1);
                        }
                        return null;
                    }
                }
        );
    }
    public String getFilterColumnById(int filterId){
        final String STATEMENT = "SELECT filter_column FROM insight_filters WHERE filter_id = ?";
        return jdbcTemplate.query(
                STATEMENT, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setInt(1, filterId);
                    }
                }, (ResultSetExtractor<String>) resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getString(1);
                    }
                    return null;
                }
        );
    }
    public int getFilterTypeById(int filterId){
        final String STATEMENT = "SELECT type_id FROM insight_filters WHERE filter_id = ?";
        Integer type = jdbcTemplate.query(
                STATEMENT, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setInt(1, filterId);
                    }
                }, (ResultSetExtractor<Integer>) resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                    return 0;
                }
        );
        if(type != null)
            return type;
        return 0;
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
