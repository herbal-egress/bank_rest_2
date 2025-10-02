-- liquibase formatted sql

-- changeset author:001_init_schema id:001
-- precondition-onFail:CONTINUE
-- precondition-sqlCheck expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users'

-- изменил: Добавлены таблицы users, roles, user_roles для аутентификации и ролевого доступа (SOLID: SRP - одна миграция для начальной схемы; OWASP: безопасное создание без уязвимостей).
-- Создание схемы public, если не существует.
CREATE SCHEMA IF NOT EXISTS public;

-- changeset author:001_init_schema id:002
-- Установка search_path для public.
SET search_path TO public;

-- changeset author:001_init_schema id:003
-- Удаление таблиц, если они существуют, для предотвращения конфликтов.
DROP TABLE IF EXISTS public.user_roles CASCADE;
DROP TABLE IF EXISTS public.transactions CASCADE;
DROP TABLE IF EXISTS public.cards CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.roles CASCADE;

-- changeset author:001_init_schema id:004
-- Создание таблицы users в схеме public.
CREATE TABLE public.users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE, -- Имя пользователя (уникальное).
    password VARCHAR(255) NOT NULL,        -- Хешированный пароль (BCrypt).
    email    VARCHAR(255) NOT NULL UNIQUE  -- Email (уникальный).
);

-- changeset author:001_init_schema id:005
-- Создание таблицы roles в схеме public.
CREATE TABLE public.roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE -- Название роли (ADMIN, USER).
);

-- changeset author:001_init_schema id:006
-- Создание таблицы user_roles (связь многие-ко-многим).
CREATE TABLE public.user_roles
(
    user_id BIGINT NOT NULL REFERENCES public.users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES public.roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- changeset author:001_init_schema id:007
-- Создание таблицы cards в схеме public.
CREATE TABLE public.cards
(
    id         BIGSERIAL PRIMARY KEY,
    number     VARCHAR(255)   NOT NULL,                                               -- Зашифрованный номер карты.
    name       VARCHAR(50)    NOT NULL,                                               -- Имя владельца.
    expiration VARCHAR(5)     NOT NULL,                                               -- Срок MM-YY.
    status     VARCHAR(20)    NOT NULL,                                               -- Enum: ACTIVE, BLOCKED, EXPIRED.
    balance    DECIMAL(19, 2) NOT NULL,                                               -- Баланс с 2 знаками.
    cvv        VARCHAR(3)     NOT NULL,                                               -- CVV 3 цифры.
    user_id    BIGINT         NOT NULL REFERENCES public.users (id) ON DELETE CASCADE -- Связь с пользователем.
);

-- changeset author:001_init_schema id:008
-- Создание индексов для оптимизации.
CREATE INDEX idx_cards_user_id ON public.cards (user_id);
CREATE INDEX idx_cards_status ON public.cards (status);

-- changeset author:001_init_schema id:009
-- Создание таблицы transactions в схеме public.
CREATE TABLE public.transactions
(
    id           BIGSERIAL PRIMARY KEY,
    from_card_id BIGINT         NOT NULL REFERENCES public.cards (id) ON DELETE CASCADE, -- Связь с картой-отправителем.
    to_card_id   BIGINT         NOT NULL REFERENCES public.cards (id) ON DELETE CASCADE, -- Связь с картой-получателем.
    amount       DECIMAL(19, 2) NOT NULL,                                                -- Сумма.
    timestamp    TIMESTAMP      NOT NULL,                                                -- Время.
    status       VARCHAR(20)    NOT NULL                                                 -- Enum: SUCCESS, FAILED.
);

-- changeset author:001_init_schema id:010
-- Создание индекса для поиска по from_card_id.
CREATE INDEX idx_transactions_from_card_id ON public.transactions (from_card_id);