DROP ALL OBJECTS;

CREATE TABLE Data (
                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      data BLOB NOT NULL
);

CREATE TABLE Data_Formats (
                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              format VARCHAR(128) NOT NULL
);

CREATE TABLE Data_To_Formats_Mappings (
                                          dataId BIGINT NOT NULL,
                                          formatId BIGINT NOT NULL,
                                          FOREIGN KEY (dataId) REFERENCES Data(id),
                                          FOREIGN KEY (formatId) REFERENCES Data_Formats(id)
);

CREATE TABLE DataProduct_AccessRights (
                                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                          accessRight VARCHAR(128) NOT NULL
);

CREATE TABLE DataProduct_Categories (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                        category VARCHAR(128) NOT NULL
);

CREATE TABLE DataProducts (
      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      shortKey VARCHAR(32) UNIQUE NOT NULL,
      title VARCHAR(128) UNIQUE NOT NULL,
      shortDescription VARCHAR(1024) NOT NULL,
      description VARCHAR(4096) NOT NULL,
      source VARCHAR(128) NOT NULL,
      sourceLink VARCHAR(1024) NOT NULL,
      lastUpdated TIMESTAMP NOT NULL,
      categoryId BIGINT NOT NULL,
      accessRightId BIGINT NOT NULL,
      image BLOB,
      dataId BIGINT,
      FOREIGN KEY (accessRightId) REFERENCES DataProduct_AccessRights(id),
      FOREIGN KEY (categoryId) REFERENCES DataProduct_Categories(id),
      FOREIGN KEY (dataId) REFERENCES Data(id)
);

CREATE TABLE Einkommensentwicklung (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   jahr VARCHAR(128) UNIQUE NOT NULL,
   insgesamt VARCHAR(128) UNIQUE NOT NULL,
   maenner VARCHAR(128) UNIQUE NOT NULL,
   frauen VARCHAR(128) UNIQUE NOT NULL
);