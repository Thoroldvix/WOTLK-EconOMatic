--liquibase formatted sql

--changeset thorold:1
INSERT INTO region (name, g2g_id)
VALUES (
        'EU',
        'ac3f85c1-7562-437e-b125-e89576b9a38e'::uuid
);
INSERT INTO region (name, g2g_id)
VALUES (
        'DE',
        'ac3f85c1-7562-437e-b125-e89576b9a38e'::uuid
);
INSERT INTO region (name, g2g_id)
VALUES (
        'ES',
        'ac3f85c1-7562-437e-b125-e89576b9a38e'::uuid
);
INSERT INTO region (name, g2g_id)
VALUES (
        'FR',
        'ac3f85c1-7562-437e-b125-e89576b9a38e'::uuid
);
INSERT INTO region (name, g2g_id)
VALUES (
        'US',
        'dfced32f-2f0a-4df5-a218-1e068cfadffa'::uuid
);
INSERT INTO region (name, g2g_id)
VALUES (
        'OCE',
        'dfced32f-2f0a-4df5-a218-1e068cfadffa'::uuid
);