package com.mse.datafabric.immobilien.dtos;

import lombok.Data;

@Data
public class ImmobilienBean {

    private String id;
    private String portalId;
    private String title;
    private Double size;
    //private Double flatSize;
    private Double rent;

}
