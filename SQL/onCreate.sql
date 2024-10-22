
-- SQL-Statements für die Methode "onCreate()", also zur Erzeugung des Datenbank-Schemas.

CREATE TABLE zaehler (
  name   TEXT    PRIMARY KEY, -- Name des Zählers, z.B. "KFZ_ALLEINE" oder "LKW".
  anzahl INTEGER DEFAULT 0
);


-- Zähler für die drei Fahrzeug-Arten anlegen.
-- Das explizite Setzen der Zähler-Werte auf "0" ist eigentlich nicht notwendig,
-- weil dies der Default-Wert für diese Spalte laut CREATE-Statement ist.
INSERT INTO zaehler (name, anzahl) VALUES ( 'KFZ_ALLEINE'  , 0 );
INSERT INTO zaehler (name, anzahl) VALUES ( 'KFZ_MITFAHRER', 0 );
INSERT INTO zaehler (name, anzahl) VALUES ( 'LKW'          , 0 );
