package com.mse.datafabric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Date;
import java.util.*;

@ShellComponent
@Component
class DataProductsProvider implements IDataProductsProvider
{
    @Autowired
    JdbcTemplate jdbcTemplate;
    public List<DataProductOverviewBean> getDataProductsOverview()
    {
        String dataProductsSql = "SELECT dp.shortKey, dp.title, dp.shortDescription, dp.lastUpdated, dpc.category, dpar.accessRight FROM DataProducts dp JOIN DataProduct_Categories dpc ON dp.categoryId = dpc.id JOIN DataProduct_AccessRights dpar ON dp.accessRightId = dpar.id";
        List<Map<String, Object>> databaseDataProducts = jdbcTemplate.queryForList(dataProductsSql);

        List<DataProductOverviewBean> dataProducts = new ArrayList<>();

        for (Map databaseDataProduct : databaseDataProducts)
        {
            DataProductOverviewBean dataProduct = new DataProductOverviewBean(
                (String)databaseDataProduct.get("shortKey"),
                (String)databaseDataProduct.get("title"),
                (String)databaseDataProduct.get("shortDescription"),
                new Date(((Timestamp) databaseDataProduct.get("lastUpdated")).getTime()),
                DataProductCategories.valueOf((String)databaseDataProduct.get("category")),
                DataProductAccessRights.valueOf((String)databaseDataProduct.get("accessRight"))
            );
            dataProducts.add(dataProduct);
        }

        return dataProducts;
    }

    public DataProductDetailBean getDataProductDetail(String shortKey) {
        String dataProductSql = "SELECT dp.title, dp.shortDescription, dp.description, dp.source, dp.sourceLink, dp.lastUpdated, dpc.category, dpar.accessRight FROM DataProducts dp JOIN DataProduct_Categories dpc ON dp.categoryId = dpc.id JOIN DataProduct_AccessRights dpar ON dp.accessRightId = dpar.id WHERE dp.shortKey = '%s'".formatted(shortKey);
        Map<String, Object> databaseDataProduct = jdbcTemplate.queryForMap(dataProductSql);

        return new DataProductDetailBean(
            shortKey,
            (String)databaseDataProduct.get("title"),
            (String)databaseDataProduct.get("shortDescription"),
            (String)databaseDataProduct.get("description"),
            (String)databaseDataProduct.get("source"),
            (String)databaseDataProduct.get("sourceLink"),
            new Date(((Timestamp) databaseDataProduct.get("lastUpdated")).getTime()),
            DataProductCategories.valueOf((String)databaseDataProduct.get("category")),
            DataProductAccessRights.valueOf((String)databaseDataProduct.get("accessRight"))
        );
    }
}
