package com.mse.datafabric;

import com.mse.datafabric.immobilien.webScraper.dtos.ScrapingContentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class DataProductRepository {

    public static final Logger LOGGER= LoggerFactory.getLogger(DataProductRepository.class);
    public final JdbcTemplate jdbcTemplate;
    private static final String UPDATE_DATE_DATA_PRODUCT = "UPDATE DATAPRODUCTS SET LASTUPDATED = ? WHERE SHORTKEY = ?";
    public DataProductRepository() {
        jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    public void updateDataProductDate(String shortKey, Date date) {
            try {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_DATE_DATA_PRODUCT);
                    ps.setDate(1, date);
                    ps.setString(2, shortKey);
                    return ps;
                });
            }
            catch (Exception e) {
                //failCounter++;
            }
    }
    public static DriverManagerDataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:h2:file:~/db/datafabric;AUTO_SERVER=true");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}
