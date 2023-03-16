package com.mse.datafabric;

import java.util.List;

interface IDataProductsProvider
{
    List<DataProductOverviewBean> getDataProductsOverview();
    DataProductDetailBean getDataProductDetail(String shortKey);
}
