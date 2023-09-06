package com.mse.datafabric.einkommensentwicklung;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class EinkommensentwicklungController {

    private final Logger log = LoggerFactory.getLogger( getClass() );
    @Autowired
    JdbcTemplate jdbcTemplate;

    @ShellMethod( "getEinkommensentwicklung" )
    @GetMapping("/DataProducts/einkommensentwicklung")
    public String getEinkommensentwicklung() throws IOException {
        List<EinkommensentwicklungBean> einkommenBeanList = new ArrayList<>();

        String dataproducts_sql = "SELECT * FROM Einkommensentwicklung";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(dataproducts_sql);

        for (Map row : rows) {
            EinkommensentwicklungBean obj = new EinkommensentwicklungBean();

            obj.setJahr((String) row.get("jahr"));
            obj.setInsgesamt((String) row.get("insgesamt"));
            obj.setMaenner((String) row.get("maenner"));
            obj.setFrauen((String) row.get("frauen"));

            einkommenBeanList.add(obj);
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(einkommenBeanList);
    }
}

