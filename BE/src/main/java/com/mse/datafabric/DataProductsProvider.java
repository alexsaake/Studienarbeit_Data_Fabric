package com.mse.datafabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.Date;
import java.util.*;

@ShellComponent
@Component
class DataProductsProvider implements IDataProductsProvider {

    private final Logger log = LoggerFactory.getLogger( getClass() );
    @Autowired
    JdbcTemplate jdbcTemplate;
    public List<IDataProductBean> getDataProducts() {
        List<IDataProductBean> dataProducts = new ArrayList<>();

        String dataproducts_sql = "SELECT * FROM Dataproducts";

        List<IDataProductBean> dataProductBeans = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(dataproducts_sql);

        for (Map row : rows) {
            DataProductBean obj = new DataProductBean();

            obj.setImage(Base64.getEncoder().encodeToString((byte[]) row.get("image")));
            obj.setTitle((String) row.get("title"));
            obj.setShortDescription(((String) row.get("shortDescription")));
            obj.setLastUpdated(new Date(((Timestamp) row.get("lastUpdated")).getTime()));
            obj.setAccessRights(DataProductAccessRights.valueOf((String) row.get("dataProductAccessRights")));
            obj.setDataProductKey((String) row.get("dataproduct_key"));
            dataProductBeans.add(obj);
        }


/*        IDataProductBean dataProduct = new DataProductBean();
        //dataProduct.setImage(Base64.getEncoder().encodeToString(LoadImage()));
        dataProduct.setTitle("Entwicklung der Bruttomonatsverdienste in Deutschland");
        dataProduct.setShortDescription("Entwicklung der durchschnittlichen Bruttomonats-verdienste ab 1991 in Deutschland");
        dataProduct.setLastUpdated(new GregorianCalendar(2022, Calendar.MARCH,24).getTime());
        dataProduct.setAccessRights(DataProductAccessRights.GRATIS);

        dataProducts.add(dataProduct);*/

        return dataProductBeans;
    }

    private byte[] loadImage() {
        byte[] image = null;
        try {
            File imageResource = new ClassPathResource("BruttomonatsverdiensteDeutschland.jpg").getFile();
            image = Files.readAllBytes(imageResource.toPath());
        }
        catch (IOException e){
            System.out.println("Could not load image " + e);
        }
        return image;
    }

    @ShellMethod( "saveImageToDbForId" ) //save-image-to-db-for-id --dataproductKey einkommensentwicklung
    private void saveImageToDbForId(String dataproductKey) {

        String sql = "UPDATE Dataproducts SET image= ? where dataproduct_key=?;";

        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql.toString(),
                        Statement.RETURN_GENERATED_KEYS);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(loadImage());
                ps.setBlob(1, inputStream);
                ps.setString(2, dataproductKey);


                return ps;
            }
        }, holder);
    }

}
