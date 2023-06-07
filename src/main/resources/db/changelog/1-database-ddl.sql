--liquibase formatted sql

--changeset thorold:1
CREATE TABLE server
(
    id          INT PRIMARY KEY,
    name        VARCHAR(32) NOT NULL,
    region      VARCHAR(3)  NOT NULL,
    faction     VARCHAR(32) NOT NULL,
    type        VARCHAR(6)  NOT NULL,
    unique_name VARCHAR(64) GENERATED ALWAYS AS ( REPLACE(REPLACE(LOWER(name), ' ', '-'), '''', '') || '-' ||
                                                  LOWER(faction)) STORED,
    locale      VARCHAR(16) NOT NULL
);

CREATE TABLE population
(
    id         BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    population INT NOT NULL,
    server_id  INT NOT NULL REFERENCES server (id),
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE server_price
(
    id         BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    server_id  INT           NOT NULL REFERENCES server (id),
    price      DECIMAL(7, 6) NOT NULL,
    updated_at TIMESTAMP     NOT NULL
);

CREATE TABLE item
(
    id             INT PRIMARY KEY,
    quality        VARCHAR(32)  NOT NULL,
    type           VARCHAR(32)  NOT NULL,
    name           VARCHAR(255) NOT NULL,
    icon           VARCHAR(255) NOT NULL,
    sell_price     BIGINT,
    required_level INT,
    item_level     INT,
    item_link      VARCHAR(255),
    unique_name    VARCHAR(64)  NOT NULL
);


-- CREATE EXTENSION pg_trgm;
-- CREATE INDEX idx_item_unique_name ON item USING gin (unique_name gin_trgm_ops);
-- CREATE INDEX idx_server_unique_name ON server USING gin (unique_name gin_trgm_ops);
