CREATE TABLE T_PHONE(
    id            SERIAL NOT NULL,
    number        VARCHAR(30) NOT NULL,
    country       VARCHAR(2) NOT NULL,
    created       TIMESTAMPTZ NOT NULL DEFAULT now(),

    UNIQUE(number),
    PRIMARY KEY (id)
);



CREATE TABLE T_ACCOUNT(
    id            SERIAL NOT NULL,
    phone_fk      BIGINT REFERENCES T_PHONE(id),
    display_name  VARCHAR(50),
    picture_url   TEXT,
    status        INT NOT NULL DEFAULT 1,
    created       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated       TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted       TIMESTAMPTZ,

    PRIMARY KEY (id)
);

CREATE OR REPLACE FUNCTION account_updated()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_account_updated
BEFORE UPDATE ON T_ACCOUNT
FOR EACH ROW
EXECUTE PROCEDURE account_updated();



CREATE TABLE T_PASSWORD(
    id            SERIAL NOT NULL,
    account_fk    BIGINT REFERENCES T_ACCOUNT(id),
    value         VARCHAR(32) NOT NULL,
    salt          VARCHAR(36) NOT NULL,
    created       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated       TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
);

CREATE OR REPLACE FUNCTION password_updated()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_password_updated
BEFORE UPDATE ON T_PASSWORD
FOR EACH ROW
EXECUTE PROCEDURE password_updated();
