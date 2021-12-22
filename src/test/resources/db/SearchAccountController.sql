INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234111', 'CM')
;

INSERT INTO T_ACCOUNT(tenant_id, id, phone_fk, display_name, picture_url, status, language, is_super_user, is_deleted)
    VALUES
        (777, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, 'fr', true, false),
        (777, 101, 101, 'John Smith', 'https://me.com/111/picture.png', 1, 'fr', true, false),
        (777, 199, null, 'Deleted', null, 3, 'en', false, true),
        (888, 200, 100, 'John Smith', 'https://me.com/111/picture.png', 1, 'fr', true, false)
    ;
