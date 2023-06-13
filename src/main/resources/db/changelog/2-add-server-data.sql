--liquibase formatted sql

--changeset thoroldvix:1 contextFilter:docker
COPY server (id, name, region, faction, type, unique_name, locale)
    FROM '/docker-entrypoint-initdb.d/server-data.csv'
    DELIMITER ','
    CSV;

--changeset thoroldvix:1 contextFilter:dev
COPY server (id, name, region, faction, type, unique_name, locale)
    FROM '/server-data.csv'
    DELIMITER ','
    CSV;