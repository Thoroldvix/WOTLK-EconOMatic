--liquibase formatted sql

--changeset thoroldvix:1
COPY item (id, quality, type, name, icon_link, slot, sell_price, required_level, item_level, item_link, unique_name,
           wowhead_link)
    FROM '/docker-entrypoint-initdb.d/item-data.csv'
    DELIMITER ','
    CSV;