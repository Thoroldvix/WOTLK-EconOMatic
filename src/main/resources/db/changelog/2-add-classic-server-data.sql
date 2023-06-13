--liquibase formatted sql

--changeset thoroldvix:1
COPY server (id, name, region, faction, type, unique_name, locale)
    FROM '/docker-entrypoint-initdb.d/server-data.csv'
    DELIMITER ','
    CSV;