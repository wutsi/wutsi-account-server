ALTER TABLE T_ACCOUNT ADD COLUMN tenant_id BIGINT;
UPDATE T_ACCOUNT SET tenant_id = 1;
ALTER TABLE T_ACCOUNT ALTER COLUMN tenant_id SET NOT NULL;
