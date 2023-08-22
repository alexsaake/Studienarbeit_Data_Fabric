package com.mse.datafabric.dataProducts.models;


public enum DataProductSQLWhitelists {
    IMMO_RENT("IMMO_DATA","IMMO_DATA.RENT","GOOGLE_MAPS_DATA ON IMMO_DATA.googleMapsDataId = GOOGLE_MAPS_DATA.dataId",null,
            new DataProductSQLFilterDTO[]{
                    new DataProductSQLFilterDTO("IMMO_DATA.CITY", "IMMO_DATA.FROMDATE", "IMMO_DATA.FROMDATE"),
                    new DataProductSQLFilterDTO("GOOGLE_MAPS_DATA.postalCode", "IMMO_DATA.FROMDATE", "IMMO_DATA.FROMDATE")
    }),
    IMMO_COUNT("IMMO_DATA",null,"GOOGLE_MAPS_DATA ON IMMO_DATA.googleMapsDataId = GOOGLE_MAPS_DATA.dataId","STATUS = 'active'",
            new DataProductSQLFilterDTO[]{
                    new DataProductSQLFilterDTO("IMMO_DATA.CITY","IMMO_DATA.FROMDATE","IMMO_DATA.FROMDATE"),
                    new DataProductSQLFilterDTO("GOOGLE_MAPS_DATA.postalCode", "IMMO_DATA.FROMDATE", "IMMO_DATA.FROMDATE")
    }),
    IMMO_SIZE("IMMO_DATA","IMMO_DATA.ROOMSIZE","GOOGLE_MAPS_DATA ON IMMO_DATA.googleMapsDataId = GOOGLE_MAPS_DATA.dataId",null,
            new DataProductSQLFilterDTO[]{
                    new DataProductSQLFilterDTO("IMMO_DATA.CITY","IMMO_DATA.FROMDATE","IMMO_DATA.FROMDATE"),
                    new DataProductSQLFilterDTO("GOOGLE_MAPS_DATA.postalCode", "IMMO_DATA.FROMDATE", "IMMO_DATA.FROMDATE")
    }),
    IMMO_CITY("IMMO_DATA","IMMO_DATA.CITY", null,null, new DataProductSQLFilterDTO[]{
            new DataProductSQLFilterDTO("IMMO_DATA.CITY","IMMO_DATA.FROMDATE","IMMO_DATA.FROMDATE")
    }),
    IMMO_ADDRESS_CITY("IMMO_DATA","IMMO_DATA.ADDRESSCITY", null,null, null),
    IMMO_ADDRESS_STREET("IMMO_DATA","IMMO_DATA.ADDRESSSTREET", null,null, null),
    IMMO_MAPS_ID("IMMO_DATA","IMMO_DATA.googleMapsDataId", null,null, null),
    MAPS_POSTAL_CODE("GOOGLE_MAPS_DATA","GOOGLE_MAPS_DATA.postalCode", "IMMO_DATA ON IMMO_DATA.googleMapsDataId = GOOGLE_MAPS_DATA.dataId","GOOGLE_MAPS_DATA.postalCode IS NOT NULL", new DataProductSQLFilterDTO[]{
            new DataProductSQLFilterDTO("IMMO_DATA.CITY","IMMO_DATA.FROMDATE","IMMO_DATA.FROMDATE")
    });
    ;


    public final String tableName;
    public final String selectColumn;
    public final String joinExpression;
    public final String filterExpression;
    public final DataProductSQLFilterDTO[] filter;
    DataProductSQLWhitelists(String tableName, String selectColumn,String joinExpression, String filterExpression, DataProductSQLFilterDTO[] filter) {
        this.tableName = tableName;
        this.selectColumn = selectColumn;
        this.joinExpression = joinExpression;
        this.filterExpression = filterExpression;
        this.filter = filter;
    }

}
