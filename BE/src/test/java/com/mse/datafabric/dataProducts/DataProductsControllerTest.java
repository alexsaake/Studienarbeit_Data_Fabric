package com.mse.datafabric.dataProducts;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.dataProducts.models.DataProductAccessRights;
import com.mse.datafabric.dataProducts.models.DataProductCategories;
import com.mse.datafabric.dataProducts.models.DataProductDetailsDto;
import com.mse.datafabric.dataProducts.models.DataProductSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyLong;

class DataProductsControllerTest
{
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
        List<DataProductSummaryDto> dataProductOverviewBeans = new ArrayList<>();
        dataProductOverviewBeans.add(new DataProductSummaryDto(1, "title", "shortDescription", new Date(0), "saa", DataProductAccessRights.gratis, DataProductCategories.Wirtschaft));
        Mockito.when(myDataProductsProviderMock.getDataProductsOverview()).thenReturn(dataProductOverviewBeans);
        String expectedResponseJSON = "[{\"id\":1,\"title\":\"title\",\"shortDescription\":\"shortDescription\",\"lastUpdated\":0,\"userName\":\"saa\",\"category\":\"Wirtschaft\",\"accessRight\":\"gratis\"}]";

        String actualResponseJSON = myDataProductsController.getDataProductsOverview();

        Assert.isTrue(expectedResponseJSON.equals(actualResponseJSON), "");
    }

    @Test
    void getDataProductDetail_ValidDataProducts_ReturnValidJSON()
    {
        DataProductDetailsDto dataProductDetailBean = new DataProductDetailsDto(1, "title", "shortDescription", new Date(0), DataProductAccessRights.gratis, DataProductCategories.Wirtschaft, "description", "source", "sourceLink","schne");
        Mockito.when(myDataProductsProviderMock.getDataProductDetail(anyLong())).thenReturn(dataProductDetailBean);
        String expectedResponseJSON = "{\"id\":1,\"title\":\"title\",\"shortDescription\":\"shortDescription\",\"description\":\"description\",\"userName\":\"schne\",\"source\":\"source\",\"sourceLink\":\"sourceLink\",\"lastUpdated\":0,\"category\":\"Wirtschaft\",\"accessRight\":\"gratis\"}";

        String actualResponseJSON = myDataProductsController.getDataProductDetail(1);

        Assert.isTrue(expectedResponseJSON.equals(actualResponseJSON), "");
    }
}