ALTER TABLE T_ACCOUNT ADD COLUMN timezone_id VARCHAR(100);

UPDATE T_ACCOUNT set timezone_id='UTC' WHERE country='GB';
UPDATE T_ACCOUNT set timezone_id='Africa/Douala' WHERE country='CM';
UPDATE T_ACCOUNT set timezone_id='America/Montreal' WHERE country='CA';
UPDATE T_ACCOUNT set timezone_id='Europe/Paris' WHERE country='FR';
