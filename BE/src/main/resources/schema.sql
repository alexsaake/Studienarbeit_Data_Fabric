DROP ALL OBJECTS;

CREATE TABLE Dataproducts (
                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              image BLOB,
                              title VARCHAR(128) UNIQUE NOT NULL,
                              shortDescription VARCHAR(2048) NOT NULL,
                              lastUpdated TIMESTAMP NOT NULL,
                              dataProductAccessRights VARCHAR(128) NOT NULL,
                              dataproduct_key VARCHAR(255)
);

CREATE TABLE Einkommensentwicklung (
                                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       jahr VARCHAR(128) UNIQUE NOT NULL,
                                       insgesamt VARCHAR(128) UNIQUE NOT NULL,
                                       maenner VARCHAR(128) UNIQUE NOT NULL,
                                       frauen VARCHAR(128) UNIQUE NOT NULL
);

//ALTER TABLE Dataproducts ADD FOREIGN KEY (dataproduct_fk) REFERENCES Einkommensentwicklung (id);
