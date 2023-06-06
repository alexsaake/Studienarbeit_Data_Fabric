package com.mse.datafabric.immobilien.webScraper;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class ScraperRepository {

    public static final Logger LOGGER= LoggerFactory.getLogger(ScraperRepository.class);
    public final JdbcTemplate jdbcTemplate;

    private static final String INSERT_INTO_IMMO_DATA = "INSERT INTO IMMO_DATA (portalId, itemId, city, title, roomSize, flatSize, rent) values (?,?,?,?,?,?,?)";

    public ScraperRepository() {
        jdbcTemplate = new JdbcTemplate(getDataSource());
    }

    public void saveDTOToDatabase(List<ScrapingContentDTO> dtoList) {
        try {
            for (ScrapingContentDTO dto: dtoList) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_INTO_IMMO_DATA);
                    ps.setString(1, dto.portalId);
                    ps.setString(2, dto.itemId);
                    ps.setString(3, dto.cityName);
                    ps.setString(4, dto.title);
                    ps.setString(5, dto.roomSize);
                    ps.setString(6, dto.flatSize);
                    ps.setString(7, dto.rent);
                    return ps;
                });
            }
            LOGGER.info("DTO successfully saved!");
        } catch (Exception e) {

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