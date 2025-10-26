--liquibase formatted sql

--changeset init:schema_1
-- изменил: Убрал precondition (redundant с IF NOT EXISTS, idempotent по паттерну Liquibase best practices).
-- добавил: Создание схемы bankrest, если не существует (атомарный changeset для SRP).
CREATE SCHEMA IF NOT EXISTS bankrest;

--changeset init:schema_2
-- добавил: Принудительный выбор схемы для сессии Liquibase (фикс ошибки "схема не выбрана", OWASP: explicit schema selection).
SET search_path TO bankrest;

--changeset init:schema_3
-- изменил: Квалифицировал все таблицы схемой для security (OWASP) и consistency.
-- добавил: Очистка таблиц в правильном порядке (CASCADE для зависимостей; idempotent с IF EXISTS).
DROP TABLE IF EXISTS bankrest.user_roles CASCADE;
DROP TABLE IF EXISTS bankrest.transactions CASCADE;
DROP TABLE IF EXISTS bankrest.cards CASCADE;
DROP TABLE IF EXISTS bankrest.users CASCADE;
DROP TABLE IF EXISTS bankrest.roles CASCADE;

--changeset init:schema_4
-- изменил: Квалифицировал схемой для избежания search_path зависимостей.
-- добавил: Создание таблицы users (основная сущность, PK и unique constraints).
CREATE TABLE bankrest.users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

--changeset init:schema_5
-- изменил: Квалифицировал схемой.
-- добавил: Создание таблицы roles (enum-like, unique name).
CREATE TABLE bankrest.roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

--changeset init:schema_6
-- изменил: Квалифицировал схемой и REFERENCES.
-- добавил: Создание junction таблицы user_roles (M:N relation, FK с CASCADE).
CREATE TABLE bankrest.user_roles
(
    user_id BIGINT NOT NULL REFERENCES bankrest.users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES bankrest.roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

--changeset init:schema_7
-- изменил: Квалифицировал схемой и REFERENCES.
-- добавил: Создание таблицы cards (с FK на users, decimal для balance).
CREATE TABLE bankrest.cards
(
    id         BIGSERIAL PRIMARY KEY,
    number     VARCHAR(255)   NOT NULL,
    name       VARCHAR(50)    NOT NULL,
    expiration VARCHAR(5)     NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    balance    DECIMAL(19, 2) NOT NULL,
    user_id    BIGINT         NOT NULL REFERENCES bankrest.users (id) ON DELETE CASCADE
);

--changeset init:schema_8
-- изменил: Квалифицировал схемой.
-- добавил: Индексы для производительности (на user_id и status).
CREATE INDEX idx_cards_user_id ON bankrest.cards (user_id);
CREATE INDEX idx_cards_status ON bankrest.cards (status);

--changeset init:schema_9
-- изменил: Квалифицировал схемой и REFERENCES.
-- добавил: Создание таблицы transactions (FK на cards, timestamp).
CREATE TABLE bankrest.transactions
(
    id           BIGSERIAL PRIMARY KEY,
    from_card_id BIGINT         NOT NULL REFERENCES bankrest.cards (id) ON DELETE CASCADE,
    to_card_id   BIGINT         NOT NULL REFERENCES bankrest.cards (id) ON DELETE CASCADE,
    amount       DECIMAL(19, 2) NOT NULL,
    timestamp    TIMESTAMP      NOT NULL,
    status       VARCHAR(20)    NOT NULL
);

--changeset init:schema_10
-- изменил: Квалифицировал схемой.
-- добавил: Индекс для transactions (на from_card_id для запросов).
CREATE INDEX idx_transactions_from_card_id ON bankrest.transactions (from_card_id);