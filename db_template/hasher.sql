--
-- File generated with SQLiteStudio v3.1.1 on Mon Mar 20 01:35:16 2017
--
-- Text encoding used: UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: hasher
DROP TABLE IF EXISTS hasher;

CREATE TABLE hasher (
    id   INTEGER      NOT NULL
                      PRIMARY KEY,
    hash VARCHAR (42) NOT NULL
);


-- Index: hasher_hash
DROP INDEX IF EXISTS hasher_hash;

CREATE UNIQUE INDEX hasher_hash ON hasher (
    "hash"
);


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
