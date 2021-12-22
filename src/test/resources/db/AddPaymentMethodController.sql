INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (200, '+237221234200', 'CM')
;

INSERT INTO T_ACCOUNT(tenant_id, id, phone_fk, display_name, picture_url, status, deleted, is_deleted)
    VALUES
        (777, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, null, false),
        (777, 200, 200, 'Roger Milla', 'https://me.com/101/picture.png', 1, null, false)
;

INSERT INTO T_PAYMENT_METHOD(id, token, owner_name, account_fk, phone_fk, type, provider, is_deleted, deleted)
    VALUES
        (200, 'b829c8f257d30fcd8dbb5933256874f7', 'Yo Man', 200, 200, 1, 1, true, now())
;
