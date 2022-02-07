CREATE TABLE T_BUSINESS_HOUR(
    id              SERIAL NOT NULL,

    account_fk      BIGINT NOT NULL REFERENCES T_ACCOUNT(id),
    day_of_week     INT NOT NULL,
    opened          BOOL NOT NULL,
    open_time       VARCHAR(5),
    close_time      VARCHAR(5),

    UNIQUE(account_fk, day_of_week),
    PRIMARY KEY (id)
)
