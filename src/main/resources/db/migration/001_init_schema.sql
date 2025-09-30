-- изменил ИИ: Добавлены таблицы users, roles, user_roles для аутентификации и ролевого доступа (SOLID: SRP - одна миграция для начальной схемы; OWASP: безопасное создание без уязвимостей).
-- Создание схемы bankrest, если не существует.
CREATE SCHEMA IF NOT EXISTS bankrest;

-- Установка search_path для bankrest.
SET search_path TO bankrest;

-- Удаление таблиц, если они существуют, для предотвращения конфликтов.
DROP TABLE IF EXISTS bankrest.user_roles CASCADE;
DROP TABLE IF EXISTS bankrest.transactions CASCADE;
DROP TABLE IF EXISTS bankrest.cards CASCADE;
DROP TABLE IF EXISTS bankrest.users CASCADE;
DROP TABLE IF EXISTS bankrest.roles CASCADE;

-- Создание таблицы users в схеме bankrest.
CREATE TABLE bankrest.users (
                                id BIGSERIAL PRIMARY KEY,
                                username VARCHAR(50) NOT NULL UNIQUE, -- Имя пользователя (уникальное).
                                password VARCHAR(255) NOT NULL,       -- Хешированный пароль (BCrypt).
                                email VARCHAR(255) NOT NULL UNIQUE    -- Email (уникальный).
);

-- Создание таблицы roles в схеме bankrest.
CREATE TABLE bankrest.roles (
                                id BIGSERIAL PRIMARY KEY,
                                name VARCHAR(20) NOT NULL UNIQUE      -- Название роли (ADMIN, USER).
);

-- Создание таблицы user_roles (связь многие-ко-многим).
CREATE TABLE bankrest.user_roles (
                                     user_id BIGINT NOT NULL REFERENCES bankrest.users(id) ON DELETE CASCADE,
                                     role_id BIGINT NOT NULL REFERENCES bankrest.roles(id) ON DELETE CASCADE,
                                     PRIMARY KEY (user_id, role_id)
);

-- Создание таблицы cards в схеме bankrest.
CREATE TABLE bankrest.cards (
                                id BIGSERIAL PRIMARY KEY,
                                number VARCHAR(255) NOT NULL,    -- Зашифрованный номер карты.
                                name VARCHAR(50) NOT NULL,       -- Имя владельца.
                                expiration VARCHAR(5) NOT NULL,  -- Срок MM-YY.
                                status VARCHAR(20) NOT NULL,     -- Enum: ACTIVE, BLOCKED, EXPIRED.
                                balance DECIMAL(19,2) NOT NULL,  -- Баланс с 2 знаками.
                                cvv VARCHAR(3) NOT NULL,         -- CVV 3 цифры.
                                user_id BIGINT NOT NULL REFERENCES bankrest.users(id) ON DELETE CASCADE -- Связь с пользователем.
);

-- Создание индексов для оптимизации.
CREATE INDEX idx_cards_user_id ON bankrest.cards(user_id);
CREATE INDEX idx_cards_status ON bankrest.cards(status);

-- Создание таблицы transactions в схеме bankrest.
CREATE TABLE bankrest.transactions (
                                       id BIGSERIAL PRIMARY KEY,
                                       from_card_id BIGINT NOT NULL REFERENCES bankrest.cards(id) ON DELETE CASCADE, -- Связь с картой-отправителем.
                                       to_card_id BIGINT NOT NULL REFERENCES bankrest.cards(id) ON DELETE CASCADE,   -- Связь с картой-получателем.
                                       amount DECIMAL(19,2) NOT NULL,                                              -- Сумма.
                                       timestamp TIMESTAMP NOT NULL,                                               -- Время.
                                       status VARCHAR(20) NOT NULL                                                 -- Enum: SUCCESS, FAILED.
);

-- Создание индекса для поиска по from_card_id.
CREATE INDEX idx_transactions_from_card_id ON bankrest.transactions(from_card_id);