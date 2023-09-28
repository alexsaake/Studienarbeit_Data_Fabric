package com.mse.datafabric.dataProducts.data.insights;

import com.mse.datafabric.utils.GoogleMapsAPI;
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

    public InsightFilterDTO getFilterById(String shortkey, int filterId) {
        String SQL = "SELECT filter_column, type_id FROM insight_filters JOIN dataproducts ON dataproducts.id = insight_filters.dataproduct_id WHERE insight_filters.filter_id = ? AND dataproducts.shortkey = ?";
        return jdbcTemplate.query(
                SQL, new PreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setInt(1, filterId);
                        preparedStatement.setString(2, shortkey);
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
    public DataProductInsightDataDTO[] getDataProductInsights(String shortkey){
        List<DataProductInsightDataDTO> dtoList = new ArrayList<>();
        String query = "SELECT * FROM dataproduct_insights JOIN dataproducts ON dataproducts.id = dataproduct_insights.dataproduct_id WHERE dataproducts.shortkey = ? ORDER BY dataproduct_insights.id";
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

                            DataProductInsightDataDTO dto = new DataProductInsightDataDTO(type,displayName, unit, column);

                            dtoList.add(dto);
                        }
                        return null;
                    }
                }
        );
        return dtoList.toArray(new DataProductInsightDataDTO[0]);
    }
    public InsightFilterDTO[] getInsightFilters(String shortkey){
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
}
