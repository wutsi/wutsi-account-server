INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234111', 'CM'),
        (102, '+237221234112', 'CM')
;

INSERT INTO T_ACCOUNT(tenant_id, id, phone_fk, display_name, picture_url, status, language, is_super_user, is_deleted, business, score)
    VALUES
        (777, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, 'fr', true, false, true, 0),
        (777, 101, 101, 'John Smith', 'https://me.com/111/picture.png', 1, 'fr', false, false, false, 0.2),
        (777, 102, 102, 'Roger Milla', 'https://me.com/111/picture.png', 1, 'fr', false, false, true, 0.1),
        (777, 199, null, 'Deleted', null, 3, 'en', false, true, true, 1),
        (888, 200, 100, 'John Smith', 'https://me.com/111/picture.png', 1, 'fr', false, false, false, 0)
    ;
