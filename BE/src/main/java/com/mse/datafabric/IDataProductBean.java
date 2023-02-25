package com.mse.datafabric;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

interface IDataProductBean {
    String getImage();
    void setImage(String image);
    String getTitle();
    void setTitle(String title);
    String getShortDescription();
    void setShortDescription(String shortDescription);
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Date getLastUpdated();
    void setLastUpdated(Date lastUpdated);
    DataProductAccessRights getAccessRights();
    void setAccessRights(DataProductAccessRights dataProductAccessRights);
    String getDataProductKey();
    void setDataProductKey(String dataProductKey);
}
