--liquibase formatted sql

--changeset thoroldvix:1
CREATE INDEX ON item (name);
CREATE INDEX ON item (type);
CREATE INDEX ON item_price (item_id, server_id);
CREATE INDEX ON item_price (updated_at DESC);