INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM'),
        (199, '+237221234199', 'CM')
;

INSERT INTO T_ACCOUNT(tenant_id, id, phone_fk, display_name, picture_url, status, deleted, is_deleted)
    VALUES
        (777, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, null, false)
;

INSERT INTO T_PAYMENT_METHOD(id, token, owner_name, account_fk, phone_fk, type, provider, is_deleted, deleted)
    VALUES
        (100, '0000-00000-100', 'Ray Sponsible', 100, 100, 1, 1, false, null),
        (101, '0000-00000-101', 'Ray Sponsible', 100, 101, 1, 2, false, null),
        (199, '0000-00000-199', 'Ray Sponsible', 100, 199, 1, 1, true, now())
;
