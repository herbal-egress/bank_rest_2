-- liquibase formatted sql
-- changeset author:002_init_data id:1
SET search_path TO bankrest;
-- changeset author:002_init_data id:2
TRUNCATE TABLE bankrest.user_roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.cards RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.users RESTART IDENTITY CASCADE;
TRUNCATE TABLE bankrest.roles RESTART IDENTITY CASCADE;
-- changeset author:002_init_data id:3
INSERT INTO bankrest.roles (id, name)
VALUES (1, 'ADMIN'),
       (2, 'USER');
-- changeset author:002_init_data id:4
INSERT INTO bankrest.users (id, username, password)
VALUES (1, 'admin', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC'),
       (2, 'user', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC');
-- changeset author:002_init_data id:5
INSERT INTO bankrest.user_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2);
-- changeset author:002_init_data id:6
-- Добавлено: обновление последовательностей после вставки данных с явными ID
SELECT setval('bankrest.users_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.users), 1));
SELECT setval('bankrest.roles_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.roles), 1));
SELECT setval('bankrest.cards_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.cards), 1));
-- changeset author:002_init_data id:7
-- Реальные номера карт (без шифрования), реальные CVV (3 цифры)
-- CardDataConverter автоматически зашифрует их при работе приложения
INSERT INTO bankrest.cards (id, number, name, expiration, status, balance, user_id)
VALUES
-- Карты администратора (2 карты)
(1, '4111111111111111', 'Admin Card', '12-27', 'ACTIVE', 10000.00, 1),
(2, '4222222222222222', 'Admin Card', '11-26', 'ACTIVE', 5000.00, 1),
-- Карты пользователя (2 карты)
(3, '4333333333333333', 'User Card', '10-25', 'ACTIVE', 1500.00, 2),
(4, '4444444444444444', 'User Card', '09-28', 'BLOCKED', 2000.00, 2);
-- changeset author:002_init_data id:8
-- Финальное обновление последовательности карт
SELECT setval('bankrest.cards_id_seq', COALESCE((SELECT MAX(id) FROM bankrest.cards), 1));