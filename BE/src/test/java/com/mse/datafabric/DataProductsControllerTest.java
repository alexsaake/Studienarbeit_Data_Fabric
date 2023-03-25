package com.mse.datafabric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;

class DataProductsControllerTest
{
    private static final String ShortKey = "shortKey";
    private DataProductsController myDataProductsController;
    private IDataProductsProvider myDataProductsProviderMock;
    private Logger myLoggerMock;

    @BeforeEach
    void setUp()
    {
        myDataProductsProviderMock = Mockito.mock(IDataProductsProvider.class);
        myLoggerMock = Mockito.mock(Logger.class);
        myDataProductsController = new DataProductsController(myDataProductsProviderMock, myLoggerMock);
    }

    @Test
    void getDataProductsOverview_ValidDataProducts_ReturnValidJSON()
    {
        List<DataProductOverviewBean> dataProductOverviewBeans = new ArrayList<>();
        dataProductOverviewBeans.add(new DataProductOverviewBean(ShortKey, "title", "shortDescription", new Date(0), DataProductCategories.Wirtschaft, DataProductAccessRights.gratis));
        Mockito.when(myDataProductsProviderMock.getDataProductsOverview()).thenReturn(dataProductOverviewBeans);
        String expectedResponseJSON = "[{\"shortKey\":\"shortKey\",\"title\":\"title\",\"shortDescription\":\"shortDescription\",\"lastUpdated\":0,\"category\":\"Wirtschaft\",\"accessRight\":\"gratis\"}]";

        String actualResponseJSON = myDataProductsController.getDataProductsOverview();

        Assert.isTrue(expectedResponseJSON.equals(actualResponseJSON));
    }

    @Test
    void getDataProductDetail_ValidDataProducts_ReturnValidJSON()
    {
        DataProductDetailBean dataProductDetailBean = new DataProductDetailBean(ShortKey, "title", "shortDescription", "description", "source", "sourceLink", new Date(0), DataProductCategories.Wirtschaft, DataProductAccessRights.gratis);
        Mockito.when(myDataProductsProviderMock.getDataProductDetail(anyString())).thenReturn(dataProductDetailBean);
        String expectedResponseJSON = "{\"shortKey\":\"shortKey\",\"title\":\"title\",\"shortDescription\":\"shortDescription\",\"description\":\"description\",\"source\":\"source\",\"sourceLink\":\"sourceLink\",\"lastUpdated\":0,\"category\":\"Wirtschaft\",\"accessRight\":\"gratis\"}";

        String actualResponseJSON = myDataProductsController.getDataProductDetail(ShortKey);

        Assert.isTrue(expectedResponseJSON.equals(actualResponseJSON));
    }
}