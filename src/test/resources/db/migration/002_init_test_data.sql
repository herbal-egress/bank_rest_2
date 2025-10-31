--liquibase formatted sql

--changeset init:data_1
-- добавил: Принудительный выбор схемы для сессии (consistency с 001_init_test_schema.sql).
SET search_path TO test;

--changeset init:data_2
-- изменил: Квалифицировал схемой.
-- добавил: Очистка данных (TRUNCATE с RESTART IDENTITY для последовательностей; idempotent).
TRUNCATE TABLE test.user_roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE test.cards RESTART IDENTITY CASCADE;
TRUNCATE TABLE test.users RESTART IDENTITY CASCADE;
TRUNCATE TABLE test.roles RESTART IDENTITY CASCADE;

--changeset init:data_3
-- изменил: Квалифицировал схемой.
-- добавил: Вставка ролей (явные ID для тестов).
INSERT INTO test.roles (id, name)
VALUES (1, 'ADMIN'),
       (2, 'USER');

--changeset init:data_4
-- изменил: Квалифицировал схемой.
-- добавил: Вставка пользователей (hashed passwords).
INSERT INTO test.users (id, username, password)
VALUES (1, 'admin', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC'),
       (2, 'user', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC');

--changeset init:data_5
-- изменил: Квалифицировал схемой.
-- добавил: Вставка user_roles (связи).
INSERT INTO test.user_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2);

--changeset init:data_6
-- изменил: Квалифицировал схемой.
-- добавил: Обновление последовательностей (после вставки с ID).
SELECT setval('test.users_id_seq', COALESCE((SELECT MAX(id) FROM test.users), 1));
SELECT setval('test.roles_id_seq', COALESCE((SELECT MAX(id) FROM test.roles), 1));
SELECT setval('test.cards_id_seq', COALESCE((SELECT MAX(id) FROM test.cards), 1));

--changeset init:data_7
-- изменил: Квалифицировал схемой.
-- добавил: Вставка карт (тестовые данные, encryption в runtime).
INSERT INTO test.cards (id, number, name, expiration, status, balance, user_id)
VALUES
    (1, '4111111111111111', 'Admin Card', '12-27', 'ACTIVE', 10000.00, 1),
    (2, '4222222222222222', 'Admin Card', '11-26', 'ACTIVE', 5000.00, 1),
    (3, '4333333333333333', 'User Card', '10-25', 'ACTIVE', 1500.00, 2),
    (4, '4444444444444444', 'User Card', '09-28', 'BLOCKED', 2000.00, 2);

--changeset init:data_8
-- изменил: Квалифицировал схемой.
-- добавил: Финальное обновление последовательности cards.
SELECT setval('test.cards_id_seq', COALESCE((SELECT MAX(id) FROM test.cards), 1));