INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM')
    ;

INSERT INTO T_ACCOUNT(tenant_id, id, phone_fk, display_name, picture_url, status, is_deleted)
    VALUES
        (777, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, false)
    ;

INSERT INTO T_BUSINESS_HOUR(account_fk, day_of_week, opened, open_time, close_time)
    VALUES
        (100, 0, false, null, null),
        (100, 1, true, '8:30', '23:30')
    ;
