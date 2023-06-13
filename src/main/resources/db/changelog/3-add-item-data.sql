--liquibase formatted sql

--changeset thoroldvix:1 contextFilter:docker
COPY item (id, quality, type, name, icon_link, slot, sell_price, required_level, item_level, item_link, unique_name,
           wowhead_link)
    FROM '/docker-entrypoint-initdb.d/item-data.csv'
    DELIMITER ','
    CSV;

--changeset thoroldvix:1 contextFilter:dev
COPY item (id, quality, type, name, icon_link, slot, sell_price, required_level, item_level, item_link, unique_name,
           wowhead_link)
    FROM '/item-data.csv'
    DELIMITER ','
    CSV;