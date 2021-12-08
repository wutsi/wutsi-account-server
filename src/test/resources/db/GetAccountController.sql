INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM')
;

INSERT INTO T_ACCOUNT(id, phone_fk, display_name, picture_url, status, language, is_super_user, is_deleted, is_transfer_secured)
    VALUES
        (100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, 'fr', true, false, false),
        (199, null, 'Deleted', null, 1, 'en', false, true, false)
    ;
