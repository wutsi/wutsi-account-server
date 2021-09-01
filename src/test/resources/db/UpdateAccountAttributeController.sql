INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM')
;

INSERT INTO T_ACCOUNT(id, phone_fk, display_name, picture_url, status, is_deleted)
    VALUES
        (100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, false),
        (101, 101, 'Thomas Nkono', 'https://me.com/101/picture.png', 1, false),
        (199, null, 'Deleted', null, 1, true)
    ;
