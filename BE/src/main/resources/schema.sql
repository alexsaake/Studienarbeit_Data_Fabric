DROP TABLE IF EXISTS Einkommensentwicklung;
DROP TABLE IF EXISTS Data_To_Formats_Mappings;
DROP TABLE IF EXISTS DataProduct_Ratings;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS DataProducts;
DROP TABLE IF EXISTS Data;
DROP TABLE IF EXISTS Data_Formats;
DROP TABLE IF EXISTS DataProduct_AccessRights;
DROP TABLE IF EXISTS DataProduct_Categories;
DROP TABLE IF EXISTS Immobilien;


CREATE TABLE Data
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    data bytea NOT NULL
);

CREATE TABLE Data_Formats
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    format VARCHAR(128) NOT NULL
);

CREATE TABLE Data_To_Formats_Mappings
(
    dataId   BIGINT NOT NULL,
    formatId BIGINT NOT NULL,
    FOREIGN KEY (dataId) REFERENCES Data (id),
    FOREIGN KEY (formatId) REFERENCES Data_Formats (id)
);

CREATE TABLE DataProduct_AccessRights
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    accessRight VARCHAR(128) NOT NULL
);

CREATE TABLE DataProduct_Categories
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category VARCHAR(128) NOT NULL
);


CREATE TABLE DataProducts
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title            VARCHAR(128)        NOT NULL,
    shortDescription VARCHAR(1024)       NOT NULL,
    description      VARCHAR(4096)       NOT NULL,
    source           VARCHAR(128)        NOT NULL,
    sourceLink       VARCHAR(1024)       NOT NULL,
    lastUpdated      TIMESTAMP           NOT NULL,
    categoryId       BIGINT              NOT NULL,
    accessRightId    BIGINT              NOT NULL,
    image            bytea,
    dataId           BIGINT,
    userId           BIGINT              NOT NULL,
    isDeleted        BOOLEAN             NOT NULL DEFAULT FALSE,
    FOREIGN KEY (accessRightId) REFERENCES DataProduct_AccessRights (id),
    FOREIGN KEY (categoryId) REFERENCES DataProduct_Categories (id),
    FOREIGN KEY (dataId) REFERENCES Data (id)
);

CREATE TABLE Users
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    firstName VARCHAR(256) NOT NULL,
    lastName VARCHAR(256) NOT NULL,
    userName VARCHAR(256) UNIQUE NOT NULL,
    email VARCHAR(256) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL,
    role VARCHAR(16) NOT NULL
);

CREATE TABLE DataProduct_Ratings
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_users BIGINT NOT NULL,
    id_dataProducts BIGINT NOT NULL,
    title VARCHAR(128),
    comment VARCHAR(128),
    rating NUMERIC(1) NOT NULL CHECK (rating>=1 AND rating<=5),
    submitted TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isEdited BOOLEAN DEFAULT FALSE,
    isDeleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_users) REFERENCES Users (id),
    FOREIGN KEY (id_dataProducts) REFERENCES DataProducts (id)
);

CREATE TABLE Einkommensentwicklung
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    jahr      VARCHAR(128) UNIQUE NOT NULL,
    insgesamt VARCHAR(128) UNIQUE NOT NULL,
    maenner   VARCHAR(128) UNIQUE NOT NULL,
    frauen    VARCHAR(128) UNIQUE NOT NULL
);

CREATE TABLE Immobilien
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    portalId VARCHAR(1024),
    title    VARCHAR(4096),
    size     FLOAT,
    rent     FLOAT
);

DROP TABLE IF EXISTS IMMO_DATA;
DROP TABLE IF EXISTS GOOGLE_MAPS_DATA;
CREATE TABLE IF NOT EXISTS GOOGLE_MAPS_DATA
(
    dataId              BIGINT NOT NULL AUTO_INCREMENT,
    placeId             VARCHAR(1024),
    locationLat         DOUBLE,
    locationLng         DOUBLE,
    postalCode          VARCHAR(1024),
    PRIMARY KEY (dataId)

);


CREATE TABLE IF NOT EXISTS IMMO_DATA
(
    portalId            VARCHAR(1024),
    itemId              VARCHAR(1024),
    date                DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    city                VARCHAR(1024),
    status              VARCHAR(1024),
    creationDate        DATE,
    title               VARCHAR(4096),
    roomSize            BIGINT,
    flatSize            BIGINT,
    rent                BIGINT,
    extraCharges        BIGINT,
    deposit             BIGINT,
    fromDate            DATE,
    addressCity         VARCHAR(1024),
    addressStreet       VARCHAR(1024),
    currencyUnit        VARCHAR(1024),
    sizeUnit            VARCHAR(1024),
    googleMapsDataId    VARCHAR(1024),
    PRIMARY KEY (portalId,itemId,date),
    FOREIGN KEY (googleMapsDataId) REFERENCES GOOGLE_MAPS_DATA (dataId)
);