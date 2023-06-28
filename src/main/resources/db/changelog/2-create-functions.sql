CREATE OR REPLACE FUNCTION trigger_set_timestamp()
    RETURNS TRIGGER AS
'
    BEGIN
        NEW.updated_at = NOW();
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION sum_if_equals(field INT, target INT)
    RETURNS INT AS
'
    BEGIN
        RETURN SUM(CASE WHEN field = target THEN 1 ELSE 0 END);
    END;
' LANGUAGE plpgsql;