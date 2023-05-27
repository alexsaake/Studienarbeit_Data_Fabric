package com.mse.datafabric.immobilien;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mse.datafabric.immobilien.dtos.ImmobilienBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class ImmobilienRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String INSERT_INTO_IMMOBILIEN = "INSERT INTO Immobilien (portalId, title, size, rent) values (?,?,?,?)";

    public void saveAllImmobilien(List<ImmobilienBean> immobilienBeanList) {
        for (ImmobilienBean immobilienBean: immobilienBeanList) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_INTO_IMMOBILIEN);
                ps.setString(1, immobilienBean.getPortalId());
                ps.setString(2, immobilienBean.getTitle());
                ps.setDouble(3, immobilienBean.getSize());
                //ps.setDouble(4, immobilienBean.getFlatSize());
                ps.setDouble(4, immobilienBean.getRent());
                return ps;
            });
        }
    }

    public void saveAllNewImmobilien(List<ImmobilienBean> immobilienBeanList) {

        List<ImmobilienBean> existingImmobilienBeanList = new ArrayList<>();

        String dataproducts_sql = "SELECT * FROM Immobilien";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(dataproducts_sql);

        for (Map row : rows) {
            ImmobilienBean obj = new ImmobilienBean();

            obj.setId(String.valueOf(row.get("id")));
            obj.setPortalId((String) row.get("portalId"));
            obj.setTitle((String) row.get("title"));
            obj.setSize((Double) row.get("size"));
            //obj.setFlatSize((Double) row.get("flatSize"));
            obj.setRent((Double) row.get("rent"));

            existingImmobilienBeanList.add(obj);
        }

        for (ImmobilienBean immobilienBean: immobilienBeanList) {
            if (existingImmobilienBeanList.stream().filter(immobilie -> Objects.equals(immobilie.getPortalId(), immobilienBean.getPortalId())).findFirst().orElse(null) == null) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_INTO_IMMOBILIEN);
                    ps.setString(1, immobilienBean.getPortalId());
                    ps.setString(2, immobilienBean.getTitle());
                    ps.setDouble(3, immobilienBean.getSize());
                    //ps.setDouble(4, immobilienBean.getFlatSize());
                    ps.setDouble(4, immobilienBean.getRent());
                    return ps;
                });
            }
        }
    }
    public  List<ImmobilienBean> getImmobilien() throws IOException {
        List<ImmobilienBean> immobilienBeanList = new ArrayList<>();

        String dataproducts_sql = "SELECT * FROM Immobilien";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(dataproducts_sql);

        for (Map row : rows) {
            ImmobilienBean obj = new ImmobilienBean();

            obj.setId(String.valueOf(row.get("id")));
            obj.setPortalId((String) row.get("portalId"));
            obj.setTitle((String) row.get("title"));
            obj.setSize((Double) row.get("size"));
            //obj.setFlatSize((Double) row.get("flatSize"));
            obj.setRent((Double) row.get("rent"));

            immobilienBeanList.add(obj);
        }

        //ObjectMapper mapper = new ObjectMapper();
        //return mapper.writeValueAsString(immobilienBeanList);
        return immobilienBeanList;
    }
}
