package com.mse.datafabric;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class DataProductsProviderTest
{
    private DataProductsProvider myDataProductsProvider;
    private JdbcTemplate myJdbcTemplateMock;

    @BeforeEach
    void setUp()
    {
        myJdbcTemplateMock = Mockito.mock(JdbcTemplate.class);
        myDataProductsProvider = new DataProductsProvider(myJdbcTemplateMock);
    }

    @Test
    void getDataProductsOverview_ValidDataProducts_ReturnValidDataProductOverviewBeanList()
    {
        Map<String, Object> databaseDataProduct = Map.of("shortKey","shortKey","title", "title","shortDescription","shortDescription","lastUpdated", new Timestamp(0),"category","Wirtschaft","accessRight","gratis");
        List<Map<String, Object>> databaseDataProducts = new ArrayList<>();
        databaseDataProducts.add(databaseDataProduct);
        Mockito.when(myJdbcTemplateMock.queryForList(anyString())).thenReturn(databaseDataProducts);
        DataProductOverviewBean expectedDataProduct = new DataProductOverviewBean("shortKey", "title", "shortDescription", new Date(0), DataProductCategories.Wirtschaft, DataProductAccessRights.gratis);
        List<DataProductOverviewBean> expectedDataProducts = new ArrayList<>();
        expectedDataProducts.add(expectedDataProduct);

        List<DataProductOverviewBean> actualDataProducts = myDataProductsProvider.getDataProductsOverview();

        Assert.isTrue(expectedDataProducts.equals(actualDataProducts));
    }

    @Test
    void getDataProductDetail_ValidDataProduct_ReturnValidDataProductDetailBean()
    {
        Map<String, Object> databaseDataProduct = Map.of("shortKey","shortKey","title", "title","shortDescription","shortDescription","description","description","source","source","sourceLink","sourceLink","lastUpdated", new Timestamp(0),"category","Wirtschaft","accessRight","gratis");
        Mockito.when(myJdbcTemplateMock.queryForMap(anyString())).thenReturn(databaseDataProduct);
        DataProductOverviewBean expectedDataProduct = new DataProductDetailBean("shortKey", "title", "shortDescription", "description", "source", "sourceLink", new Date(0), DataProductCategories.Wirtschaft, DataProductAccessRights.gratis);

        DataProductDetailBean actualDataProduct = myDataProductsProvider.getDataProductDetail("shortKey");

        Assert.isTrue(expectedDataProduct.equals(actualDataProduct));
    }
}