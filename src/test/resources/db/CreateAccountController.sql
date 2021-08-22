INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM')
;

INSERT INTO T_ACCOUNT(phone_fk) VALUES(101);
