INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM')
;

INSERT INTO T_ACCOUNT(tenant_id, id, phone_fk, display_name, picture_url, status, is_deleted)
    VALUES
        (777, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, false),
        (777, 101, 101, 'Thomas Nkono', 'https://me.com/101/picture.png', 1, false),
        (777, 199, null, 'Deleted', null, 1, true)
    ;

INSERT INTO T_BUSINESS_HOUR(account_fk, day_of_week, opened, open_time, close_time)
    VALUES
        (101, 0, false, null, null);
