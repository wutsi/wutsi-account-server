INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM')
;

INSERT INTO T_ACCOUNT(tenant_id, id, phone_fk, display_name, picture_url, status, language, is_super_user, is_deleted, is_transfer_secured, business, retail)
    VALUES
        (777, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, 'fr', true, false, false, true, true),
        (777, 199, null, 'Deleted', null, 1, 'en', false, true, false, false, false)
    ;
