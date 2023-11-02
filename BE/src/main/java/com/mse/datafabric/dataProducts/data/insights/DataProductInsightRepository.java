package com.mse.datafabric.dataProducts.data.insights;

import com.mse.datafabric.utils.GoogleMapsAPI;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DataProductInsightRepository {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;

    public InsightFilterDTO getFilterById(int id, int filterId) {
        String SQL = "SELECT filter_column, type_id FROM insight_filters JOIN dataproducts ON dataproducts.id = insight_filters.dataproduct_id WHERE insight_filters.filter_id = ? AND dataproducts.id = ?";
        return jdbcTemplate.query(
                SQL, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setInt(1, filterId);
                        preparedStatement.setInt(2, id);
                    }
                },new ResultSetExtractor<>() {
                    public InsightFilterDTO extractData(ResultSet resultSet) throws SQLException,
                            DataAccessException {
                        if (resultSet.next()) {
                            String column = resultSet.getString(1);
                            int type = resultSet.getInt(2);
                            return new InsightFilterDTO(column, type);
                        }
                        return null;
                    }
                }
        );
    }
    public DataProductInsightDataDTO[] getDataProductInsights(int id){
        List<DataProductInsightDataDTO> dtoList = new ArrayList<>();
        String query = "SELECT * FROM dataproduct_insights JOIN dataproducts ON dataproducts.id = dataproduct_insights.dataproduct_id WHERE dataproducts.id = ? ORDER BY dataproduct_insights.id";
        jdbcTemplate.query(
                query, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setInt(1, id);
                    }
                },new ResultSetExtractor<>() {
                    public Object extractData(ResultSet resultSet) throws SQLException,
                            DataAccessException {
                        while (resultSet.next()) {
                            int type = resultSet.getInt("type");
                            String displayName = resultSet.getString("display_name");
                            String unit = resultSet.getString("unit");
                            String column = resultSet.getString("dataproduct_column");

                            DataProductInsightDataDTO dto = new DataProductInsightDataDTO(type,displayName, unit, column);

                            dtoList.add(dto);
                        }
                        return null;
                    }
                }
        );
        return dtoList.toArray(new DataProductInsightDataDTO[0]);
    }
    public InsightFilterDTO[] getInsightFilters(int id){
        List<InsightFilterDTO> dtoList = new ArrayList<>();
        String query = "SELECT * FROM insight_filters JOIN dataproducts ON dataproducts.id = insight_filters.dataproduct_id WHERE dataproducts.id = ? ORDER BY insight_filters.type_id DESC";
        jdbcTemplate.query(
                query, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setInt(1, id);
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
    public Map<String, String>[] getInsightTypes(){
        final String STATEMENT = "SELECT type_id, type_name FROM insight_types ORDER BY type_id";
        try {
            return jdbcTemplate.query(STATEMENT, this::getKeyValue);
        }
        catch (Exception e) {
            return null;
        }
    }
    public Map<String, String>[] getFilterTypes(){
        final String STATEMENT = "SELECT type_id, type_name FROM insight_filter_types ORDER BY type_id DESC";
        try {
            return jdbcTemplate.query(STATEMENT, this::getKeyValue);
        }
        catch (Exception e) {
            return null;
        }
    }
    public boolean insertInsights(int id, DataProductInsightDataDTO[] dto){
        final String STATEMENT = "INSERT INTO dataproduct_insights (type, display_name, unit, dataproduct_column, dataproduct_id) VALUES (?, ?, ?, ?, (SELECT id FROM dataproducts WHERE id = ?))";
        try {
            for(int i = 0;i< dto.length;i++){
                int finalI = i;
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(STATEMENT);
                    ps.setInt(1, dto[finalI].type);
                    ps.setString(2, dto[finalI].displayName);
                    ps.setString(3, dto[finalI].unit);
                    ps.setString(4, dto[finalI].dataProductColumn);
                    ps.setInt(5, id);
                    return ps;
                });
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public boolean deleteInsights(int id){
        final String STATEMENT = "DELETE FROM dataproduct_insights USING dataproducts WHERE dataproduct_insights.dataproduct_id = dataproducts.id AND dataproducts.id = ?";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(STATEMENT);
                ps.setInt(1, id);
                return ps;
            });
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public DataProductInsightDataDTO[] getInsightsData(int id){
        final String STATEMENT = "SELECT type, display_name, unit, dataproduct_column, dataproduct_id FROM dataproduct_insights JOIN dataproducts ON dataproduct_insights.dataproduct_id = dataproducts.id WHERE dataproducts.id = ?";
        try {
            return jdbcTemplate.query(
                    STATEMENT, new PreparedStatementSetter() {
                        public void setValues(PreparedStatement ps) throws SQLException {
                            ps.setInt(1, id);
                        }
                    },new ResultSetExtractor<>() {
                        public DataProductInsightDataDTO[] extractData(ResultSet rs) throws SQLException,
                                DataAccessException {
                            List<DataProductInsightDataDTO> list = new ArrayList<>();
                            while (rs.next()) {
                                list.add( new DataProductInsightDataDTO(
                                        rs.getInt(1),
                                        rs.getString(2),
                                        rs.getString(3),
                                        rs.getString(4)
                                ));
                            }
                            return list.toArray(new DataProductInsightDataDTO[0]);
                        }
                    }
            );
        }
        catch (Exception e) {
            return null;
        }
    }
    public InsightFilterDTO[] getInsightFiltersData(int id){
        final String STATEMENT = "SELECT type_id, filter_column, display_name, dataproduct_id FROM insight_filters JOIN dataproducts ON insight_filters.dataproduct_id = dataproducts.id WHERE dataproducts.id = ?";
        try {
            return jdbcTemplate.query(
                    STATEMENT, new PreparedStatementSetter() {
                        public void setValues(PreparedStatement ps) throws SQLException {
                            ps.setInt(1, id);
                        }
                    },new ResultSetExtractor<>() {
                        public InsightFilterDTO[] extractData(ResultSet rs) throws SQLException,
                                DataAccessException {
                            List<InsightFilterDTO> list = new ArrayList<>();
                            while (rs.next()) {
                                list.add( new InsightFilterDTO(
                                        id,
                                        rs.getString(3),
                                        rs.getString(2),
                                        rs.getInt(1)
                                ));
                            }
                            return list.toArray(new InsightFilterDTO[0]);
                        }
                    }
            );
        }
        catch (Exception e) {
            return null;
        }
    }
    public boolean insertInsightsFilter(int id, InsightFilterDTO[] dto){
        final String STATEMENT = "INSERT INTO insight_filters (type_id, filter_column, display_name, dataproduct_id) VALUES (?, ?, ?, (SELECT id FROM dataproducts WHERE id = ?))";
        try {
            for(int i = 0;i< dto.length;i++){
                int finalI = i;
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(STATEMENT);
                    ps.setInt(1, dto[finalI].filterType);
                    ps.setString(2, dto[finalI].filterColumn);
                    ps.setString(3, dto[finalI].displayName);
                    ps.setInt(4, id);
                    return ps;
                });
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public boolean deleteInsightsFilter(int id){
        final String STATEMENT = "DELETE FROM insight_filters USING dataproducts WHERE dataproducts.id = insight_filters.dataproduct_id AND dataproducts.id = ?";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(STATEMENT);
                ps.setInt(1, id);
                return ps;
            });
        }
        catch (Exception e) {
            return false;
        }
        return true;
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
}
