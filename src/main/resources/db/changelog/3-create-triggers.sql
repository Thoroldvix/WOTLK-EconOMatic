--liquibase formatted sql

--changeset thoroldvix:1
CREATE TRIGGER set_timestamp
    BEFORE INSERT OR UPDATE
    ON item_price
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp
    BEFORE INSERT OR UPDATE
    ON population
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp
    BEFORE INSERT OR UPDATE
    ON gold_price
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();