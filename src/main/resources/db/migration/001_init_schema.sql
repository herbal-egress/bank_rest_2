--liquibase formatted sql

--changeset init:schema_1
-- изменил: Убрал precondition (redundant с IF NOT EXISTS, idempotent по паттерну Liquibase best practices).
-- добавил: Создание схемы bankrest, если не существует (атомарный changeset для SRP).
CREATE SCHEMA IF NOT EXISTS bankrest;

--changeset init:schema_2
-- добавил: Принудительный выбор схемы для сессии Liquibase (фикс ошибки "схема не выбрана", OWASP: explicit schema selection).
SET search_path TO bankrest;