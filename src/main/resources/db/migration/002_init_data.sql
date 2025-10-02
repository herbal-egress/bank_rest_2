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
       (2, 'user', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC'),
       (3, 'test1', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC'),
       (4, 'test2', '$2a$12$Ydg5seG59XD1A1CF7CqXPe6EZbqREkxK9HeLxoIRo2cA8FWBfxdNC');

-- changeset author:002_init_data id:5
INSERT INTO bankrest.user_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (4, 2);

-- changeset author:002_init_data id:6
INSERT INTO bankrest.cards (id, number, name, expiration, status, balance, cvv, user_id)
VALUES (1, 'ENCRYPTED_4111111111111111', 'Admin Card', '12-27', 'ACTIVE', 10000.00, '123', 1),
       (2, 'ENCRYPTED_4222222222222222', 'User Card 1', '11-26', 'ACTIVE', 5000.00, '456', 2),
       (3, 'ENCRYPTED_4333333333333333', 'User Card 2', '10-25', 'BLOCKED', 1500.00, '789', 2),
       (4, 'ENCRYPTED_4444444444444444', 'Test1 Card', '09-28', 'ACTIVE', 2000.00, '321', 3),
       (5, 'ENCRYPTED_4555555555555555', 'Test2 Card', '08-29', 'EXPIRED', 0.00, '654', 4);

-- changeset author:002_init_data id:7
INSERT INTO bankrest.cards (id, number, name, expiration, status, balance, cvv, user_id)
VALUES (26, 'ENCRYPTED_4666666666666626', 'User Premium Gold', '12-27', 'ACTIVE', 12000.00, '126', 2),
       (27, 'ENCRYPTED_4666666666666627', 'User Travel Card', '11-28', 'ACTIVE', 8000.50, '227', 2),
       (28, 'ENCRYPTED_4666666666666628', 'User Business', '10-29', 'BLOCKED', 0.00, '328', 2),
       (29, 'ENCRYPTED_4666666666666629', 'User Savings', '09-30', 'ACTIVE', 25000.75, '429', 2),
       (30, 'ENCRYPTED_4666666666666630', 'User Emergency', '08-31', 'ACTIVE', 5000.00, '530', 2),
       (31, 'ENCRYPTED_4666666666666631', 'User Online Shopping', '07-32', 'EXPIRED', 100.00, '631', 2),
       (32, 'ENCRYPTED_4666666666666632', 'User Family', '06-33', 'ACTIVE', 15000.25, '732', 2),
       (33, 'ENCRYPTED_4666666666666633', 'User Investment', '05-34', 'ACTIVE', 30000.00, '833', 2);

-- changeset author:002_init_data id:8
INSERT INTO bankrest.cards (id, number, name, expiration, status, balance, cvv, user_id)
VALUES (34, 'ENCRYPTED_4777777777777734', 'Test1 Platinum', '12-27', 'ACTIVE', 18000.00, '134', 3),
       (35, 'ENCRYPTED_4777777777777735', 'Test1 Debit Card', '11-28', 'ACTIVE', 7000.50, '235', 3),
       (36, 'ENCRYPTED_4777777777777736', 'Test1 Credit Line', '10-29', 'BLOCKED', 0.00, '336', 3),
       (37, 'ENCRYPTED_4777777777777737', 'Test1 Vacation Fund', '09-30', 'ACTIVE', 12000.75, '437', 3),
       (38, 'ENCRYPTED_4777777777777738', 'Test1 Backup Card', '08-31', 'ACTIVE', 3000.00, '538', 3),
       (39, 'ENCRYPTED_4777777777777739', 'Test1 Old Expired', '07-32', 'EXPIRED', 50.00, '639', 3),
       (40, 'ENCRYPTED_4777777777777740', 'Test1 Main Account', '06-33', 'ACTIVE', 22000.25, '740', 3);

-- changeset author:002_init_data id:9
INSERT INTO bankrest.cards (id, number, name, expiration, status, balance, cvv, user_id)
VALUES (41, '4888888888888841', 'Test2 Gold Premium', '12-27', 'ACTIVE', 16000.00, '141', 4),
       (42, '4888888888888842', 'Test2 Travel Card', '11-28', 'ACTIVE', 9000.50, '242', 4),
       (43, '4888888888888843', 'Test2 Blocked Card', '10-29', 'BLOCKED', 0.00, '343', 4),
       (44, '4888888888888844', 'Test2 Savings Account', '09-30', 'ACTIVE', 28000.75, '444', 4),
       (45, '4888888888888845', 'Test2 Emergency Fund', '08-31', 'ACTIVE', 6000.00, '545', 4),
       (46, '4888888888888846', 'Test2 Expired Card', '07-32', 'EXPIRED', 75.00, '646', 4),
       (47, '4888888888888847', 'Test2 Family Card', '06-33', 'ACTIVE', 14000.25, '747', 4),
       (48, '4888888888888848', 'Test2 Investment Card', '05-34', 'ACTIVE', 32000.00, '848', 4);