-- добавленный код: Создание схемы bankrest.
CREATE SCHEMA IF NOT EXISTS bankrest;
-- добавленный код: Установка схемы по умолчанию.
SET search_path TO bankrest;
-- добавленный код: Таблица cards (пустая, будет доработана в этапе 2).
CREATE TABLE IF NOT EXISTS cards
(
    id BIGSERIAL PRIMARY KEY
);
-- добавленный код: Таблица transactions (пустая, будет доработана).
CREATE TABLE IF NOT EXISTS transactions
(
    id BIGSERIAL PRIMARY KEY
);