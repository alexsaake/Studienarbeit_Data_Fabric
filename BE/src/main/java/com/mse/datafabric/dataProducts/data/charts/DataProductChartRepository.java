package com.mse.datafabric.dataProducts.data.charts;

import com.mse.datafabric.dataProducts.data.insights.DataProductInsightDataDTO;
import com.mse.datafabric.dataProducts.data.insights.InsightFilterDTO;
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
public class DataProductChartRepository {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    public DataProductChartsDTO[] getDataProductCharts(long id){
        List<DataProductChartsDTO> dtoList = new ArrayList<>();
        String query = "SELECT * FROM dataproduct_charts WHERE dataproduct_charts.dataproduct_id = ? ORDER BY dataproduct_charts.id";
        jdbcTemplate.query(
                query, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, id);
                    }
                },new ResultSetExtractor<>() {
                    public Object extractData(ResultSet resultSet) throws SQLException,
                            DataAccessException {
                        while (resultSet.next()) {
                            DataProductChartsDTO dto = new DataProductChartsDTO(
                                    resultSet.getInt("id"),
                                    resultSet.getInt("type"),
                                    resultSet.getString("x_axis_name"),
                                    resultSet.getString("y_axis_name"),
                                    resultSet.getString("x_axis_unit"),
                                    resultSet.getString("y_axis_unit"),
                                    resultSet.getString("x_axis_dataproduct_column"),
                                    resultSet.getString("display_name"),
                                    resultSet.getInt("y_value_type"),
                                    resultSet.getInt("fill_chart")
                            );
                            dto.datasets = getDataProductChartsDatasets(dto);
                            dtoList.add(dto);
                        }
                        return null;
                    }
                }
        );
        return dtoList.toArray(new DataProductChartsDTO[0]);
    }
    public DataProductChartsDatasetDTO[] getDataProductChartsDatasets(DataProductChartsDTO chart){
        List<DataProductChartsDatasetDTO> dtoList = new ArrayList<>();
        String query = "SELECT * FROM dataproduct_charts_datasets WHERE dataproduct_charts_id = ?";
        jdbcTemplate.query(
                query, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, chart.id);
                    }
                },new ResultSetExtractor<>() {
                    public Object extractData(ResultSet resultSet) throws SQLException,
                            DataAccessException {
                        while (resultSet.next()) {
                            DataProductChartsDatasetDTO dto = new DataProductChartsDatasetDTO(
                                    chart.id,
                                    resultSet.getString("display_name"),
                                    resultSet.getString("y_axis_dataproduct_column")
                            );
                            dtoList.add(dto);
                        }
                        return null;
                    }
                }
        );
        return dtoList.toArray(new DataProductChartsDatasetDTO[0]);
    }

    public Map<String, String>[] getChartTypes(){
        final String STATEMENT = "SELECT chart_id, chart_name FROM chart_types ORDER BY chart_id";
        try {
            return jdbcTemplate.query(STATEMENT, this::getKeyValue);
        }
        catch (Exception e) {
            return null;
        }
    }
    public boolean insertCharts(long id, DataProductChartsDTO[] dto){
        Long chartId;
        final String STATEMENT = "INSERT INTO dataproduct_charts (type, x_axis_name, y_axis_name, x_axis_unit, y_axis_unit, x_axis_dataproduct_column, dataproduct_id, display_name, y_value_type, fill_chart) VALUES (?, ?, ?, ?, ?,?,?,?,?,?) RETURNING id";
        try {
            for(int i = 0;i< dto.length;i++){
                int finalI = i;
                chartId = jdbcTemplate.query(
                        STATEMENT, new PreparedStatementSetter() {
                            public void setValues(PreparedStatement ps) throws SQLException {
                                ps.setInt(1, dto[finalI].type);
                                ps.setString(2, dto[finalI].xAxisName);
                                ps.setString(3, dto[finalI].yAxisName);
                                ps.setString(4, dto[finalI].xAxisUnit);
                                ps.setString(5, dto[finalI].yAxisUnit);
                                ps.setString(6, dto[finalI].xAxisDataproductColumn);
                                ps.setLong(7, id);
                                ps.setString(8, dto[finalI].displayName);
                                ps.setInt(9, dto[finalI].yValueType);
                                ps.setInt(10, dto[finalI].fillChart);
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
                if(chartId == null)
                    return false;
                if(!insertChartDatasets(dto[finalI].datasets, chartId))
                    return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public boolean insertChartDatasets(DataProductChartsDatasetDTO[] dto, Long chartId){
        final String STATEMENT = "INSERT INTO dataproduct_charts_datasets (dataproduct_charts_id, display_name, y_axis_dataproduct_column) VALUES (?, ?, ?)";
        try {
            for(int i = 0;i< dto.length;i++){
                if(dto[i].displayName.equals(""))
                    continue;
                int finalI = i;
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(STATEMENT);
                    ps.setLong(1, chartId);
                    ps.setString(2, dto[finalI].displayName);
                    ps.setString(3, dto[finalI].yAxisDataproductColumn);
                    return ps;
                });
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public boolean deleteCharts(long id){
        final String STATEMENT = "DELETE FROM dataproduct_charts WHERE dataproduct_charts.dataproduct_id = ?";
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
