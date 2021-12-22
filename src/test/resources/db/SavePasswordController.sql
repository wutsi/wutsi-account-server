INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM')
;

INSERT INTO T_ACCOUNT(tenant_id, id, phone_fk, display_name, picture_url, status)
    VALUES
        (777, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1),
        (777, 101, null, 'Deleted', null, 1)
    ;

INSERT INTO T_PASSWORD(account_fk, value, salt, created, updated)
    VALUES
        (101, 'update-me', 'this-is-a-salt', '2011-01-01', '2011-01-01')
    ;
