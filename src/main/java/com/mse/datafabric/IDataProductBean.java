package com.mse.datafabric;

import java.util.Date;

interface IDataProductBean {
    String getImage();
    void setImage(String image);
    String getTitle();
    void setTitle(String title);
    String getShortDescription();
    void setShortDescription(String shortDescription);
    Date getLastUpdated();
    void setLastUpdated(Date lastUpdated);
    DataProductAccessRights getAccessRights();
    void setAccessRights(DataProductAccessRights dataProductAccessRights);
}
