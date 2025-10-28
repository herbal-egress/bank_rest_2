--liquibase formatted sql

--changeset init:data_1
-- добавил: Принудительный выбор схемы для сессии (consistency с 001_init_test_schema.sql).
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






--changeset init:data_11
-- изменил: Квалифицировал схемой.
-- добавил: Очистка данных (TRUNCATE с RESTART IDENTITY для последовательностей; idempotent).
TRUNCATE TABLE bankrest.user_roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.cards RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.users RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.roles RESTART IDENTITY CASCADE;

--changeset init:data_12
-- изменил: Квалифицировал схемой.
-- добавил: Вставка ролей (явные ID для тестов).
INSERT INTO bankrest.roles (id, name)
VALUES (1, 'ADMIN'),
       (2, 'USER');

--changeset init:data_13
-- изменил: Квалифицировал схемой.
-- добавил: Вставка пользователей (hashed passwords).
INSERT INTO bankrest.users (id, username, password)
VALUES (1, 'admin', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC'),
       (2, 'user', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC');

--changeset init:data_14
-- изменил: Квалифицировал схемой.
-- добавил: Вставка user_roles (связи).
INSERT INTO bankrest.user_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2);

--changeset init:data_15
-- изменил: Квалифицировал схемой.
-- добавил: Обновление последовательностей (после вставки с ID).
SELECT setval('bankrest.users_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.users), 1));
SELECT setval('bankrest.roles_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.roles), 1));
SELECT setval('bankrest.cards_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.cards), 1));

--changeset init:data_16
-- изменил: Квалифицировал схемой.
-- добавил: Вставка карт (тестовые данные, encryption в runtime).
INSERT INTO bankrest.cards (id, number, name, expiration, status, balance, user_id)
VALUES
    (1, '4111111111111111', 'Admin Card', '12-27', 'ACTIVE', 10000.00, 1),
    (2, '4222222222222222', 'Admin Card', '11-26', 'ACTIVE', 5000.00, 1),
    (3, '4333333333333333', 'User Card', '10-25', 'ACTIVE', 1500.00, 2),
    (4, '4444444444444444', 'User Card', '09-28', 'BLOCKED', 2000.00, 2);

--changeset init:data_17
-- изменил: Квалифицировал схемой.
-- добавил: Финальное обновление последовательности cards.
SELECT setval('bankrest.cards_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.cards), 1));