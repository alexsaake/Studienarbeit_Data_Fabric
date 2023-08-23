package com.mse.datafabric.immobilien.webScraper;

import com.mse.datafabric.immobilien.webScraper.dtos.ScrapingContentDTO;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class ScraperRepository {

    public static final Logger LOGGER= LoggerFactory.getLogger(ScraperRepository.class);
    @Autowired
    public JdbcTemplate jdbcTemplate;

    private static final String INSERT_INTO_IMMO_DATA = "INSERT INTO IMMO_DATA (portalId, itemId, city, status, creationDate, title, roomSize, flatSize, rent, extraCharges, deposit, fromDate, addressCity, addressStreet, currencyUnit,sizeUnit) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public ScraperRepository() {

    }

    public boolean saveDTOToDatabase(List<ScrapingContentDTO> dtoList) {
        int failCounter = 0;

        for (ScrapingContentDTO dto: dtoList) {
            if(dto.dtoSaved)
                continue;
            else
                dto.dtoSaved = true;
            if(dto.title == null) {
                failCounter++;
                continue;
            }
            if(dto.title.equals("")) {
                continue;
            }
            try {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_INTO_IMMO_DATA);
                    ps.setString(1, dto.portalId);
                    ps.setString(2, dto.itemId);
                    ps.setString(3, dto.cityName);
                    ps.setString(4, dto.status);
                    ps.setString(5, dto.creationDate);
                    ps.setString(6, dto.title);
                    ps.setString(7, dto.roomSize);
                    ps.setString(8, dto.flatSize);
                    ps.setString(9, dto.rent);
                    ps.setString(10, dto.extraCharges);
                    ps.setString(11, dto.deposit);
                    ps.setString(12, dto.from);
                    ps.setString(13, dto.addressCity);
                    ps.setString(14, dto.addressStreet);
                    ps.setString(15, dto.currencyUnit);
                    ps.setString(16, dto.sizeUnit);
                    return ps;
                });
            }
            catch (Exception e) {
                //failCounter++;
            }
        }
        if (failCounter > 5 )
            return false;

        LOGGER.info("DTO successfully saved!");
        return true;
    }
}
