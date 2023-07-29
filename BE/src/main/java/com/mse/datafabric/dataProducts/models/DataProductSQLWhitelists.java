package com.mse.datafabric.dataProducts.models;


public enum DataProductSQLWhitelists {
    IMMO_RENT("IMMO_DATA","RENT",null, new DataProductSQLFilterDTO("CITY","FROMDATE","FROMDATE")),
    IMMO_COUNT("IMMO_DATA",null,"STATUS = 'active'", new DataProductSQLFilterDTO("CITY","FROMDATE","FROMDATE")),
    IMMO_SIZE("IMMO_DATA","ROOMSIZE",null, new DataProductSQLFilterDTO("CITY","FROMDATE","FROMDATE"));

    public final String tableName;
    public final String selectColumn;
    public final String filterExpression;
    public final DataProductSQLFilterDTO filter;
    DataProductSQLWhitelists(String tableName, String selectColumn, String filterExpression, DataProductSQLFilterDTO filter) {
        this.tableName = tableName;
        this.selectColumn = selectColumn;
        this.filterExpression = filterExpression;
        this.filter = filter;
    }

}
