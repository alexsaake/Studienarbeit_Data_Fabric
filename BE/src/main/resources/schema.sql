DROP TABLE IF EXISTS Einkommensentwicklung;
DROP TABLE IF EXISTS Data_To_Formats_Mappings;
DROP TABLE IF EXISTS DataProduct_Ratings;
DROP TABLE IF EXISTS DataProducts;
DROP TABLE IF EXISTS Data;
DROP TABLE IF EXISTS Data_Formats;
DROP TABLE IF EXISTS DataProduct_AccessRights;
DROP TABLE IF EXISTS DataProduct_Categories;
DROP TABLE IF EXISTS Immobilien;


CREATE TABLE Data
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    data BLOB NOT NULL
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
    shortKey         VARCHAR(32) UNIQUE  NOT NULL,
    title            VARCHAR(128) UNIQUE NOT NULL,
    shortDescription VARCHAR(1024)       NOT NULL,
    description      VARCHAR(4096)       NOT NULL,
    source           VARCHAR(128)        NOT NULL,
    sourceLink       VARCHAR(1024)       NOT NULL,
    lastUpdated      TIMESTAMP           NOT NULL,
    categoryId       BIGINT              NOT NULL,
    accessRightId    BIGINT              NOT NULL,
    image            BLOB,
    dataId           BIGINT,
    FOREIGN KEY (accessRightId) REFERENCES DataProduct_AccessRights (id),
    FOREIGN KEY (categoryId) REFERENCES DataProduct_Categories (id),
    FOREIGN KEY (dataId) REFERENCES Data (id)
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
    FOREIGN KEY (id_users) REFERENCES User (id),
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
CREATE TABLE IF NOT EXISTS IMMO_DATA
(
    portalId        VARCHAR(1024),
    itemId          VARCHAR(1024),
    date            DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    city            VARCHAR(1024),
    status          VARCHAR(1024),
    creationDate    DATE,
    title           VARCHAR(4096),
    roomSize        VARCHAR(1024),
    flatSize        VARCHAR(1024),
    rent            VARCHAR(1024),
    extraCharges    VARCHAR(1024),
    deposit         VARCHAR(1024),
    fromDate        DATE,
    addressCity     VARCHAR(1024),
    addressStreet   VARCHAR(1024),
    currencyUnit    VARCHAR(1024),
    sizeUnit        VARCHAR(1024),
    PRIMARY KEY (portalId,itemId,date)
);