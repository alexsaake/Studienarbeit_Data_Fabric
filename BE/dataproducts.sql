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

INSERT INTO Einkommensentwicklung (jahr, insgesamt, maenner, frauen) VALUES
('2021','4 100', '4 275','3 699'),
('2020','3 975', '4 146','3 578'),
('2019','3 994', '4 181','3 559'),
('2018','3 880', '4 075','3 432'),
('2017','3 771', '3 964','3 330'),
('2016','3 703', '3 898','3 258'),
('2015','3 612', '3 810','3 161'),
('2014','3 527', '3 728','3 075'),
('2013','3 449', '3 645','3 007'),
('2012','3 391', '3 595','2 925'),
('2011','3 311', '3 508','2 861'),
('2010','3 227', '3 416','2 791'),
('2009','3 141', '3 320','2 729'),
('2008','3 103', '3 294','2 661'),
('2007','3 023', '3 211','2 590'),
('2006','2 950', '3 138','2 522'),
('2005','2 901', '3 088','2 475'),
('2004','2 846', '3 034','2 421'),
('2003','2 783', '2 972','2 359'),
('2002','2 701', '2 889','2 286'),
('2001','2 617', '2 800','2 216'),
('2000','2 551', '2 732','2 150'),
('1999','2 518', '2 713','2 105'),
('1998','2 447', '2 639','2 039'),
('1997','2 389', '2 580','1 985'),
('1996','2 344', '2 539','1 933'),
('1995','2 281', '2 470','1 858'),
('1994','2 185', '2 370','1 774'),
('1993','2 103', '2 281','1 704'),
('1992','2 003', '2 188','1 602'),
('1991','1 832', '2 010','1 440');

INSERT INTO Dataproducts (title, shortDescription, lastUpdated, dataProductAccessRights, dataproduct_key) VALUES
                                                      ('Entwicklung der Bruttomonatsverdienste in Deutschland', 'Entwicklung der durchschnittlichen Bruttomonats-verdienste ab 1991 in Deutschland', TIMESTAMP '2022-12-24 06:57:57', 'GRATIS', 'einkommensentwicklung');
