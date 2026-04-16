CREATE TABLE patient (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     version BIGINT NOT NULL DEFAULT 0,
     nom VARCHAR (50) NOT NULL,
     prenom VARCHAR (50) NOT NULL,
     date_naissance DATE NOT NULL,
     genre CHAR(1) NOT NULL CHECK (genre IN ('M', 'F')),
     adresse VARCHAR (100),
     telephone VARCHAR (12),
     CONSTRAINT uk_patient_identite UNIQUE (nom, prenom, date_naissance)
);

INSERT INTO patient (
    nom,
    prenom,
    date_naissance,
    genre,
    adresse,
    telephone
) VALUES (
  'TestNone',
  'Test',
  '1966-12-31',
  'F',
  '1 Brookside St',
  '100-222-3333'
),
(
  'TestBorderline',
  'Test',
  '1945-06-24',
  'M',
  '2 High St',
  '200-333-4444'
),
(
  'TestInDanger',
  'Test',
  '2004-06-18',
  'M',
  '3 Club Road',
  '300-444-5555'
),
(
  'TestEarlyOnset',
  'Test',
  '2002-06-28',
  'F',
  '4 Valley Dr',
  '400-555-6666'
);
