package com.mse.datafabric.dataProducts;

import com.mse.datafabric.dataProducts.models.DataProductAccessRights;
import com.mse.datafabric.dataProducts.models.DataProductCategories;
import com.mse.datafabric.dataProducts.models.DataProductDetailDto;
import com.mse.datafabric.dataProducts.models.DataProductOverviewDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

class DataProductsServiceTest
{
    private DataProductsService myDataProductsService;
    private JdbcTemplate myJdbcTemplateMock;

    @BeforeEach
    void setUp()
    {
        myJdbcTemplateMock = Mockito.mock(JdbcTemplate.class);
        myDataProductsService = new DataProductsService(myJdbcTemplateMock);
    }

    @Test
    void getDataProductsOverview_ValidDataProducts_ReturnValidDataProductOverviewBeanList()
    {
        Map<String, Object> databaseDataProduct = Map.of("shortKey","shortKey","title", "title","shortDescription","shortDescription","lastUpdated", new Timestamp(0),"category","Wirtschaft","accessRight","gratis");
        List<Map<String, Object>> databaseDataProducts = new ArrayList<>();
        databaseDataProducts.add(databaseDataProduct);
        Mockito.when(myJdbcTemplateMock.queryForList(anyString())).thenReturn(databaseDataProducts);
        DataProductOverviewDto expectedDataProduct = new DataProductOverviewDto("shortKey", "title", "shortDescription", new Date(0), DataProductAccessRights.gratis, DataProductCategories.Wirtschaft);
        List<DataProductOverviewDto> expectedDataProducts = new ArrayList<>();
        expectedDataProducts.add(expectedDataProduct);

        List<DataProductOverviewDto> actualDataProducts = myDataProductsService.getDataProductsOverview();

        Assert.isTrue(expectedDataProducts.equals(actualDataProducts), "");
    }

    @Test
    void getDataProductDetail_ValidDataProduct_ReturnValidDataProductDetailBean()
    {
        Map<String, Object> databaseDataProduct = Map.of("shortKey","shortKey","title", "title","shortDescription","shortDescription","description","description","source","source","sourceLink","sourceLink","lastUpdated", new Timestamp(0),"category","Wirtschaft","accessRight","gratis");
        Mockito.when(myJdbcTemplateMock.queryForMap(anyString())).thenReturn(databaseDataProduct);
        DataProductOverviewDto expectedDataProduct = new DataProductDetailDto("shortKey", "title", "shortDescription", new Date(0), DataProductAccessRights.gratis, DataProductCategories.Wirtschaft, "description", "source", "sourceLink");

        DataProductDetailDto actualDataProduct = myDataProductsService.getDataProductDetail("shortKey");

        Assert.isTrue(expectedDataProduct.equals(actualDataProduct), "");
    }
}