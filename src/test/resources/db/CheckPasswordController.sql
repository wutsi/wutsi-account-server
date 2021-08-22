INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM')
;

INSERT INTO T_ACCOUNT(id, phone_fk, display_name, picture_url, status)
    VALUES
        (100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1),
        (101, null, 'Deleted', null, 1)
    ;

INSERT INTO T_PASSWORD(account_fk, value, salt, created, updated)
    VALUES
        (100, 'de7770f025c80b904e771d4cdbc43ded', 'this-is-a-salt', '2011-01-01', '2011-01-01')
    ;
