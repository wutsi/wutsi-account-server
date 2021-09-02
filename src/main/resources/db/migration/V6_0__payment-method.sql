CREATE TABLE T_PAYMENT_METHOD(
    id            SERIAL NOT NULL,
    token         VARCHAR(36) NOT NULL,
    account_fk    BIGINT NOT NULL REFERENCES T_ACCOUNT(id),
    phone_fk      BIGINT REFERENCES T_PHONE(id),
    owner_name    VARCHAR(100) NOT NULL,
    type          INT NOT NULL DEFAULT 0,
    provider      INT NOT NULL DEFAULT 0,
    is_deleted    BOOLEAN NOT NULL DEFAULT false,
    created       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated       TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted       TIMESTAMPTZ,

    UNIQUE (token),
    PRIMARY KEY (id)
);

CREATE OR REPLACE FUNCTION payment_method_updated()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_payment_method_updated
BEFORE UPDATE ON T_PAYMENT_METHOD
FOR EACH ROW
EXECUTE PROCEDURE payment_method_updated();
