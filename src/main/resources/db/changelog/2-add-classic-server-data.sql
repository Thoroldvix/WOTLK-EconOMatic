--liquibase formatted sql

--changeset thoroldvix:1
copy server(id, name, region, faction, type, unique_name, locale) from '/server.csv' delimiter ',' csv;