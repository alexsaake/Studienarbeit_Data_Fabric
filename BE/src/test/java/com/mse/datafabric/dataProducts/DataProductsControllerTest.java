package com.mse.datafabric.dataProducts;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.dataProducts.models.DataProductAccessRights;
import com.mse.datafabric.dataProducts.models.DataProductCategories;
import com.mse.datafabric.dataProducts.models.DataProductDetailDto;
import com.mse.datafabric.dataProducts.models.DataProductOverviewDto;
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
    private IDataProductsService myDataProductsProviderMock;
    private AuthenticationService myAuthenticationService;
    private Logger myLoggerMock;

    @BeforeEach
    void setUp()
    {
        myDataProductsProviderMock = Mockito.mock(IDataProductsService.class);
        myLoggerMock = Mockito.mock(Logger.class);
        myAuthenticationService = Mockito.mock(AuthenticationService.class);
        myDataProductsController = new DataProductsController(myDataProductsProviderMock, myAuthenticationService, myLoggerMock);
    }

    @Test
    void getDataProductsOverview_ValidDataProducts_ReturnValidJSON()
    {
        List<DataProductOverviewDto> dataProductOverviewBeans = new ArrayList<>();
        dataProductOverviewBeans.add(new DataProductOverviewDto(ShortKey, "title", "shortDescription", new Date(0), DataProductAccessRights.gratis, DataProductCategories.Wirtschaft));
        Mockito.when(myDataProductsProviderMock.getDataProductsOverview()).thenReturn(dataProductOverviewBeans);
        String expectedResponseJSON = "[{\"shortKey\":\"shortKey\",\"title\":\"title\",\"shortDescription\":\"shortDescription\",\"lastUpdated\":0,\"category\":\"Wirtschaft\",\"accessRight\":\"gratis\"}]";

        String actualResponseJSON = myDataProductsController.getDataProductsOverview();

        Assert.isTrue(expectedResponseJSON.equals(actualResponseJSON), "");
    }

    @Test
    void getDataProductDetail_ValidDataProducts_ReturnValidJSON()
    {
        DataProductDetailDto dataProductDetailBean = new DataProductDetailDto(ShortKey, "title", "shortDescription", new Date(0), DataProductAccessRights.gratis, DataProductCategories.Wirtschaft, "description", "source", "sourceLink","schne");
        Mockito.when(myDataProductsProviderMock.getDataProductDetail(anyString())).thenReturn(dataProductDetailBean);
        String expectedResponseJSON = "{\"shortKey\":\"shortKey\",\"title\":\"title\",\"shortDescription\":\"shortDescription\",\"description\":\"description\",\"source\":\"source\",\"sourceLink\":\"sourceLink\",\"lastUpdated\":0,\"category\":\"Wirtschaft\",\"accessRight\":\"gratis\",\"userName\":\"schne\"}";

        String actualResponseJSON = myDataProductsController.getDataProductDetail(ShortKey);

        Assert.isTrue(expectedResponseJSON.equals(actualResponseJSON), "");
    }
}