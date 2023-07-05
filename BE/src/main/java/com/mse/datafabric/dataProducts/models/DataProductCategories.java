package com.mse.datafabric.dataProducts.models;

public enum DataProductCategories {
    Wirtschaft("Wirtschaft"),
    Immobilien("Immobilien");

    public String value;
    DataProductCategories(String value) {
        this.value = value;
    }
}
