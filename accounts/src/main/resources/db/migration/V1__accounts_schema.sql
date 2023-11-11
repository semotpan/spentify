CREATE TABLE IF NOT EXISTS accounts
(
    id            BINARY(16) PRIMARY KEY,
    creation_date TIMESTAMP    NOT NULL DEFAULT NOW(),
    email_address VARCHAR(256) NOT NULL UNIQUE,
    first_name    VARCHAR(255),
    last_name     VARCHAR(255)
)
;
