-- изменил ИИ: Явно указана схема bankrest для всех таблиц и индексов (например, bankrest.cards вместо cards), чтобы устранить ошибку "no schema has been selected to create in" (SOLID: SRP - миграция отвечает за начальную схему; OWASP: безопасное создание без уязвимостей).
-- Создание схемы bankrest, если не существует.
CREATE SCHEMA IF NOT EXISTS bankrest;

-- Установка search_path для bankrest (для совместимости).
SET search_path TO bankrest;

-- Удаление таблиц, если они существуют, для предотвращения конфликтов.
DROP TABLE IF EXISTS bankrest.transactions CASCADE;
DROP TABLE IF EXISTS bankrest.cards CASCADE;

-- Создание таблицы cards в схеме bankrest.
CREATE TABLE bankrest.cards (
                                id BIGSERIAL PRIMARY KEY,
                                number VARCHAR(255) NOT NULL,    -- Зашифрованный номер карты.
                                name VARCHAR(50) NOT NULL,       -- Имя владельца.
                                expiration VARCHAR(5) NOT NULL,  -- Срок MM-YY.
                                status VARCHAR(20) NOT NULL,     -- Enum: ACTIVE, BLOCKED, EXPIRED.
                                balance DECIMAL(19,2) NOT NULL,  -- Баланс с 2 знаками.
                                ccv VARCHAR(3) NOT NULL,         -- CCV 3 цифры.
                                user_id BIGINT NOT NULL           -- ID пользователя для ролевого доступа.
);

-- Создание индексов для оптимизации (по user_id и status для поиска "своих карт").
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

-- Создание индекса для поиска по from_card_id (для логирования транзакций).
CREATE INDEX idx_transactions_from_card_id ON bankrest.transactions(from_card_id);