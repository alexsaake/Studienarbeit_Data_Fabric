package com.mse.datafabric;

import java.util.List;

interface IDataProductsProvider {
    List<IDataProductBean> getDataProducts();
    byte[] getDataProductImage(String dataproduct_key);
}
