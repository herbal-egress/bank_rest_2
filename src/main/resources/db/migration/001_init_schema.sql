--liquibase formatted sql

--changeset init:schema_1
--precondition-on-fail: CONTINUE
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = 'bankrest'
-- добавил: Создание схемы bankrest, если не существует (атомарный changeset для SRP).
CREATE SCHEMA IF NOT EXISTS bankrest;

--changeset init:schema_2
-- добавил: Установка search_path для сессии (временная, но для миграций).
SET search_path TO bankrest;

--changeset init:schema_3
--precondition-on-fail: CONTINUE
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'bankrest' AND table_name = 'users'
-- добавил: Очистка таблиц в правильном порядке (CASCADE для зависимостей).
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS cards CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

--changeset init:schema_4
-- добавил: Создание таблицы users (основная сущность, PK и unique constraints).
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

--changeset init:schema_5
-- добавил: Создание таблицы roles (enum-like, unique name).
CREATE TABLE roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

--changeset init:schema_6
-- добавил: Создание junction таблицы user_roles (M:N relation, FK с CASCADE).
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

--changeset init:schema_7
-- добавил: Создание таблицы cards (с FK на users, decimal для balance).
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

--changeset init:schema_8
-- добавил: Индексы для производительности (на user_id и status).
CREATE INDEX idx_cards_user_id ON cards (user_id);
CREATE INDEX idx_cards_status ON cards (status);

--changeset init:schema_9
-- добавил: Создание таблицы transactions (FK на cards, timestamp).
CREATE TABLE transactions
(
    id           BIGSERIAL PRIMARY KEY,
    from_card_id BIGINT         NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    to_card_id   BIGINT         NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    amount       DECIMAL(19, 2) NOT NULL,
    timestamp    TIMESTAMP      NOT NULL,
    status       VARCHAR(20)    NOT NULL
);

--changeset init:schema_10
-- добавил: Индекс для transactions (на from_card_id для запросов).
CREATE INDEX idx_transactions_from_card_id ON transactions (from_card_id);