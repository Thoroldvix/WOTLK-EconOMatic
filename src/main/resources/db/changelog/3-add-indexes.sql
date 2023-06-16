--liquibase formatted sql

--changeset thoroldvix:1
CREATE INDEX ON item USING btree (name);
CREATE INDEX ON item USING btree (type);
CREATE INDEX ON item_price USING btree (item_id, server_id);