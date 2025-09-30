-- Создание схемы, если не существует bankrest.
CREATE SCHEMA IF NOT EXISTS bankrest;
-- Установка search_path для bankrest.
SET search_path TO bankrest;
-- Создание таблицы cards (карты), если не существует.
CREATE TABLE IF NOT EXISTS cards
(
    id         BIGSERIAL PRIMARY KEY,
    number     VARCHAR(255)   NOT NULL, -- Зашифрованный номер карты.
    name       VARCHAR(50)    NOT NULL, -- Имя владельца.
    expiration VARCHAR(5)     NOT NULL, -- Срок MM-YY.
    status     VARCHAR(20)    NOT NULL, -- Enum: ACTIVE, BLOCKED, EXPIRED.
    balance    DECIMAL(19, 2) NOT NULL, -- Баланс с 2 знаками.
    ccv        VARCHAR(3)     NOT NULL, -- CCV 3 цифры.
    user_id    BIGINT         NOT NULL  -- ID пользователя для ролевого доступа.
);
-- Создание индексов для оптимизации (по user_id и status для поиска "своих карт").
CREATE INDEX IF NOT EXISTS idx_cards_user_id ON cards (user_id);
CREATE INDEX IF NOT EXISTS idx_cards_status ON cards (status);
-- Создание таблицы transactions (транзакции), если не существует.
CREATE TABLE IF NOT EXISTS transactions
(
    id           BIGSERIAL PRIMARY KEY,
    from_card_id BIGINT         NOT NULL REFERENCES cards (id) ON DELETE CASCADE, -- Связь с картой-отправителем.
    to_card_id   BIGINT         NOT NULL REFERENCES cards (id) ON DELETE CASCADE, -- Связь с картой-получателем.
    amount       DECIMAL(19, 2) NOT NULL,                                         -- Сумма.
    timestamp    TIMESTAMP      NOT NULL,                                         -- Время.
    status       VARCHAR(20)    NOT NULL                                          -- Enum: SUCCESS, FAILED.
);
-- Создание индекса для поиска по from_card_id (для логирования транзакций).
CREATE INDEX IF NOT EXISTS idx_transactions_from_card_id ON transactions (from_card_id);