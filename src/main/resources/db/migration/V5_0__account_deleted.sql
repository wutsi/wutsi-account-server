ALTER TABLE T_ACCOUNT ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE T_ACCOUNT RENAME COLUMN super_user TO is_super_user;