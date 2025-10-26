--liquibase formatted sql

--changeset init:data_1
-- добавил: Установка search_path (повтор для независимости файла).
SET search_path TO bankrest;

--changeset init:data_2
--precondition-on-fail: CONTINUE
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM bankrest.users
-- добавил: Очистка данных (TRUNCATE с RESTART IDENTITY для последовательностей).
TRUNCATE TABLE bankrest.user_roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.cards RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.users RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.roles RESTART IDENTITY CASCADE;

--changeset init:data_3
-- добавил: Вставка ролей (явные ID для тестов).
INSERT INTO bankrest.roles (id, name)
VALUES (1, 'ADMIN'),
       (2, 'USER');

--changeset init:data_4
-- добавил: Вставка пользователей (hashed passwords).
INSERT INTO bankrest.users (id, username, password)
VALUES (1, 'admin', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC'),
       (2, 'user', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC');

--changeset init:data_5
-- добавил: Вставка user_roles (связи).
INSERT INTO bankrest.user_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2);

--changeset init:data_6
-- добавил: Обновление последовательностей (после вставки с ID).
SELECT setval('bankrest.users_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.users), 1));
SELECT setval('bankrest.roles_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.roles), 1));
SELECT setval('bankrest.cards_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.cards), 1));

--changeset init:data_7
-- добавил: Вставка карт (тестовые данные, encryption в runtime).
INSERT INTO bankrest.cards (id, number, name, expiration, status, balance, user_id)
VALUES
    (1, '4111111111111111', 'Admin Card', '12-27', 'ACTIVE', 10000.00, 1),
    (2, '4222222222222222', 'Admin Card', '11-26', 'ACTIVE', 5000.00, 1),
    (3, '4333333333333333', 'User Card', '10-25', 'ACTIVE', 1500.00, 2),
    (4, '4444444444444444', 'User Card', '09-28', 'BLOCKED', 2000.00, 2);

--changeset init:data_8
-- добавил: Финальное обновление последовательности cards.
SELECT setval('bankrest.cards_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.cards), 1));