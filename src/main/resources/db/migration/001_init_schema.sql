-- liquibase formatted sql
-- changeset author:001_init_schema id:1
-- precondition-onFail:CONTINUE
-- precondition-sqlCheck expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'bankrest' AND table_name = 'users'
CREATE SCHEMA IF NOT EXISTS bankrest;
-- changeset author:001_init_schema id:2
SET search_path TO bankrest;
-- changeset author:001_init_schema id:3
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS cards CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
-- changeset author:001_init_schema id:4
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
-- changeset author:001_init_schema id:5
CREATE TABLE roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);
-- changeset author:001_init_schema id:6
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);
-- changeset author:001_init_schema id:7
CREATE TABLE cards
(
    id         BIGSERIAL PRIMARY KEY,
    number     VARCHAR(255)   NOT NULL,
    name       VARCHAR(50)    NOT NULL,
    expiration VARCHAR(5)     NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    balance    DECIMAL(19, 2) NOT NULL,
    user_id    BIGINT         NOT NULL REFERENCES users (id) ON DELETE CASCADE
);
-- changeset author:001_init_schema id:8
CREATE INDEX idx_cards_user_id ON cards (user_id);
CREATE INDEX idx_cards_status ON cards (status);
-- changeset author:001_init_schema id:9
CREATE TABLE transactions
(
    id           BIGSERIAL PRIMARY KEY,
    from_card_id BIGINT         NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    to_card_id   BIGINT         NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    amount       DECIMAL(19, 2) NOT NULL,
    timestamp    TIMESTAMP      NOT NULL,
    status       VARCHAR(20)    NOT NULL
);
-- changeset author:001_init_schema id:10
CREATE INDEX idx_transactions_from_card_id ON transactions (from_card_id);