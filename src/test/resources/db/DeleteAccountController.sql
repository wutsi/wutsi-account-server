INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM')
;

INSERT INTO T_ACCOUNT(id, phone_fk, display_name, picture_url, status, deleted)
    VALUES
        (100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, null),
        (199, null, 'Deleted', null, 3, '2011-01-01')
    ;
