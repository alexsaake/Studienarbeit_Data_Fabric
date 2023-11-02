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

INSERT INTO DataProduct_AccessRights (accessRight) VALUES ('gratis');

INSERT INTO DataProduct_Categories (category) VALUES ('Wirtschaft');
INSERT INTO DataProduct_Categories (category) VALUES ('Immobilien');

INSERT INTO Data_Formats (format) VALUES ('json');
INSERT INTO Data_Formats (format) VALUES ('xlsx');

INSERT INTO DataProducts (title, shortDescription, description, source, sourceLink, lastUpdated, categoryId, accessRightId) VALUES
    ('Entwicklung der Bruttomonatsverdienste in Deutschland', 'Entwicklung der durchschnittlichen Bruttomonatsverdienste ab 1991 in Deutschland', 'Der Datensatz enthält durchschnittliche Bruttoverdienste in Deutschland ab 1991. Der Datensatz enthält eine Untergliederung der Einkommen nach Geschlecht', 'Statistisches Bundesamt', 'https://www.destatis.de/DE/Themen/Arbeit/Verdienste/Verdienste-Verdienstunterschiede/Tabellen/liste-bruttomonatsverdienste.html#134694', TIMESTAMP '2022-12-24 06:57:57', (SELECT id FROM DataProduct_Categories WHERE category = 'Wirtschaft'), (SELECT id FROM DataProduct_AccessRights WHERE accessRight = 'gratis'));

INSERT INTO DataProducts (title, shortDescription, description, source, sourceLink, lastUpdated, categoryId, accessRightId) VALUES
    ('Immobilien Inserate', 'Immobilien Inserate (z.B. für Nürnberg von WG-Gesucht seit April 2023)', 'Der Datensatz enthält grundlegende Daten von Immobilienanzeigen', 'Immobilienanzeigen WG-Gesucht', '', TIMESTAMP '2023-04-12 00:00:00', (SELECT id FROM DataProduct_Categories WHERE category = 'Immobilien'), (SELECT id FROM DataProduct_AccessRights WHERE accessRight = 'gratis'));

INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '8184799', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'große 1 Zimmerwohnung mit 2 Balkone und Stellplatz - 1-Zimmer-Wohnung in Nürnberg-Katzwang', 51, null, 730, 100, 1460, '2023-07-01', '90455 Nürnberg Katzwang', 'Am Klosterbach 7', '€', 'm²', '1');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '9884562', '2023-07-01', 'Nürnberg', 'active', '2023-06-30', 'Expats willkommen! Ruhiges Rundum-sorglos-Appartement in der Stadt - 1-Zimmer-Wohnung in Nürnberg-Röthenbach b.Schweinau', 26, null, 500, 200, 1600, '2023-10-01', '90449 Nürnberg Röthenbach b.Schweinau', 'Ansbacher Straße 132', '€', 'm²', '2');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '10137479', '2023-07-01', 'Nürnberg', 'active', '2023-06-27', 'Alle Kosten inbegriffen - 1 Zimmer Apartment voll möbliert - inklusive highspeed Internet, (Öko)Strom, Heizung u. Warm- u. Kaltwasser. Zwischen den Fakultäten Campus nah im Herzen von Nürnberg in bester Wohnlage. Wöhrder Wiese und Wöhrder See vor der Türe - 1-Zimmer-Wohnung in Nürnberg-Gleißbühl', 24, null, 428, 170, 1500, '2023-08-01', '90402 Nürnberg Gleißbühl', 'Bahnhofstraße 79', '€', 'm²', '3');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '10119653', '2023-07-01', 'Nürnberg', 'active', '2023-06-29', 'Einzimmerwohnung Stadtzentrum - 1-Zimmer-Wohnung in Nürnberg', 20, null, 300, 140, 1020, '2023-07-01', '90409 Nürnberg', 'Bayreuther Straße 19', '€', 'm²', '4');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '10190788', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'Nürnberg Zerzabelshof - 1 Zimmer Appartment - 1-Zimmer-Wohnung in Nürnberg-Zerzabelshof', 29, null, 650, 0, 1300, '2023-06-30', '90480 Nürnberg Zerzabelshof', 'Bingstraße 30', '€', 'm²', '5');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '9738963', '2023-07-01', 'Nürnberg', 'active', '2023-06-28', 'Entspannte, gemischte 5er WG auf großem Raum - 1-Zimmer-Wohnung in Nürnberg', 15, null, 480, null, null, '2023-08-01', '90408 Nürnberg', 'Bucher Straße', '€', 'm²', '6');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '3041012', '2023-07-01', 'Nürnberg', 'active', '2023-06-30', 'Möbliertes Appartement  tageweise, wochenweise oder monatsweise zu vermieten, ab 3 Nächte - 1-Zimmer-Wohnung in Nürnberg-Eibach', 22, null, 40, 0, 100, '2023-06-29', '90451 Nürnberg Eibach', 'Eibacher Hauptstr.', '€', 'm²', '7');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '9039358', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'Studenten Apartments in Nürnberg - Wöhrd - 1-Zimmer-Wohnung in Nürnberg-Wöhrd', 25, null, 470, 120, 920, '2023-06-30', '90489 Nürnberg Wöhrd', 'Hirsvogelstr. 18', '€', 'm²', '8');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '10192503', '2023-07-01', 'Nürnberg', 'active', '2023-06-29', 'Studentenapartment Nähe Nürnberger Altstadt - nur 250 m zur Universität (Sebalder Höfe) ab 01.07. zu vermieten - 1-Zimmer-Wohnung in Nürnberg-Sebald', 34, null, 500, 60, 1500, '2023-06-28', '90403 Nürnberg Sebald', 'Kupferschmiedshof', '€', 'm²', '9');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '9034427', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'Luxuriöses 1 Zimmer Apartment Lorenzkirche, Sebaldus. Komplett möbliert! September 2023 - Juli 2024, Verlängerung aber möglich! - 1-Zimmer-Wohnung in Nürnberg-Sebald', 45, null, 850, 90, 1700, '2023-09-01', '90403 Nürnberg Sebald', 'Martin-Treu-Str.', '€', 'm²', '10');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '9159166', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'möbliertes Studentenappartment 20m² - 1-Zimmer-Wohnung in Nürnberg-Wöhrd', 20, null, 600, 0, 200, '2023-07-27', '90489 Nürnberg Wöhrd', 'Meisterleinsplatz 1', '€', 'm²', '11');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '10212554', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'Neu-Bau 1 Zimmer Wohnung Nürnberg Ost - 1-Zimmer-Wohnung in Nürnberg', 27, null, 640, 85, 1920, '2023-08-01', '90482 Nürnberg', 'Ostendstraße 115', '€', 'm²', '12');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '9724729', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'Möbliertes Studentenapartment (Neubau) - 1-Zimmer-Wohnung in Nürnberg', 17, null, 400, 91, 1200, '2023-10-01', '90478 Nürnberg', 'Peterstraße 36', '€', 'm²', '13');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '9852971', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'VOLLMÖBLIERTES APARTMENT – CAMPUS LIVING NÜRNBERG - 1-Zimmer-Wohnung in Nürnberg-Zerzabelshof', 26, null, 558, 165, 1674, '2023-07-01', '90478 Nürnberg Zerzabelshof', 'Regensburger Str. 14', '€', 'm²', '14');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '7713742', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'Einzimmer Appartement nähe Wöhrder Wiese - 1-Zimmer-Wohnung in Nürnberg-Marienvorstadt', 32, null, 375, 100, 900, '2023-07-02', '90402 Nürnberg Marienvorstadt', 'Reindelstraße 4', '€', 'm²', '15');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '6168871', '2023-07-01', 'Nürnberg', 'active', '2023-06-28', 'möbl. Appartement für Mitarbeiter*innen von BA, HZA, BAMF, Klinikum Süd und Referendar*innen - 1-Zimmer-Wohnung in Nürnberg-Gleißhammer', 23, null, 620, 0, 800, '2023-08-01', '90478 Nürnberg Gleißhammer', 'Schultheißallee', '€', 'm²', '16');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '8707708', '2023-07-01', 'Nürnberg', 'active', '2023-06-30', '1 Zimmer-Wohnung mit Garage in der Nähe Mercado (größtes Shopping Centre) in Nürnberg - 1-Zimmer-Wohnung in Nürnberg-Schoppershof', 40, null, 720, 80, 2000, '2023-06-29', '90409 Nürnberg Schoppershof', 'Steigerwaldstraße 26', '€', 'm²', '17');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '10165694', '2023-07-01', 'Nürnberg', 'active', '2023-07-01', 'Aufgepasst: teilmöblierte 1-Zimmer-Apartments im Erstbezug! - 1-Zimmer-Wohnung in Nürnberg-Glockenhof', 19, null, 485, 125, 1455, '2023-08-01', '90478 Nürnberg Glockenhof', 'Stephanstr. 5-9', '€', 'm²', '18');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '9711086', '2023-07-01', 'München', 'active', '2023-06-30', 'Einzelzimmer in der Nähe vom Wohrder See - 1-Zimmer-Wohnung in Nürnberg-Nürnberg', 34, null, 720, 0, 750, '2023-07-17', '90489 Nürnberg Nürnberg', 'Waechterstraße 2', '€', 'm²', '19');
INSERT INTO PUBLIC.IMMO_DATA (PORTALID, ITEMID, DATE, CITY, STATUS, CREATIONDATE, TITLE, ROOMSIZE, FLATSIZE, RENT, EXTRACHARGES, DEPOSIT, FROMDATE, ADDRESSCITY, ADDRESSSTREET, CURRENCYUNIT, SIZEUNIT, GOOGLEMAPSDATAID) VALUES ('wgGesucht', '3828663', '2023-07-01', 'München', 'active', '2023-07-01', 'Schöne helle 1 Zimmerwohnung mit Balkon s/w, teilmöbliert, 1. Hochparterre - 1-Zimmer-Wohnung in Nürnberg-Langwasser', 41, null, 475, 120, 1000, '2023-08-01', '90471 Nürnberg Langwasser', 'Warmbrunner Straße 11', '€', 'm²', '20');


INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJ0WInNo9Zn0cRye-DSHgOKt0', 0, 0, '90473');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJ1fYbGpdXn0cRfxCbRXnR4lg', 0, 0, '90489');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('EipTdGVwaGFuc3RyYcOfZSA1LCA5MDQ3OCBOw7xybmJlcmcsIEdlcm1hbnkiMBIuChQKEgk5oFKjnVefRxEIBEyGPcZsJBAFKhQKEgmXqMgjnVefRxGxbkyQ4RkWUg', 0, 0, '90478');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJVXibBupXn0cRUtNMabZtFvg', 0, 0, '90409');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJ52Fatn1Xn0cRrQLeNvRhm6Y', 0, 0, '90478');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJH2VDW59Xn0cRDwxd_7vZk5I', 0, 0, '90402');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJOSwSBBNXn0cROae7F9En8Eo', 0, 0, '90478');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJmSI1wIJXn0cR-KuTeMn9is0', 0, 0, '90478');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJc_ElXydYn0cRmEH4X1FlIS4', 0, 0, '90482');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJF0Kd6ZlXn0cROimCc3PEKUE', 0, 0, '90489');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJL3PiH6VXn0cRHwS59vtuueA', 0, 0, '90403');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJeZC1vrxXn0cRmuFMgBPU47A', 0, 0, '90403');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJKftkuKNXn0cRO7uZNsXABNc', 0, 0, '90489');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('EilFaWJhY2hlciBIYXVwdHN0cmHDn2UsIE7DvHJuYmVyZywgR2VybWFueSIuKiwKFAoSCYl0MtQoUZ9HEX_UV4igN-YoEhQKEglDj8vwLVGfRxF90UF_dEuL_Q', 0, 0, null);
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('EiVCdWNoZXIgU3RyLiwgOTA0MDggTsO8cm5iZXJnLCBHZXJtYW55Ii4qLAoUChIJ5wh_8bRXn0cRXIm1LNXMeScSFAoSCRd71nO2V59HETBIxfQ12h4c', 0, 0, '90408');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJteBYOjJYn0cR2YI4YOwryQ0', 0, 0, '90480');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJlVS60L1Xn0cRC4O0ZBPvgBQ', 0, 0, '90409');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJx_e3vJ5Xn0cR3tXlUxEya9g', 0, 0, '90402');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJoUzUz7dWn0cR7duF4ekPsG0', 0, 0, '90449');
INSERT INTO PUBLIC.GOOGLE_MAPS_DATA (PLACEID, LOCATIONLAT, LOCATIONLNG, POSTALCODE) VALUES ('ChIJ5WfgEZdQn0cRZHHgEI5QBLI', 0, 0, '90455');
