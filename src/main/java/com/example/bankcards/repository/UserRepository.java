package com.example.bankcards.repository;

import com.example.bankcards.entity.User; // добавленный код: импорт сущности User.
import org.springframework.data.jpa.repository.JpaRepository; // добавленный код: импорт для базового репозитория JPA.
import org.springframework.stereotype.Repository; // добавленный код: аннотация для репозитория.

import java.util.Optional; // добавленный код: импорт для Optional.

@Repository // добавленный код: делает класс репозиторием (Spring: сканирование).
public interface UserRepository extends JpaRepository<User, Long> { // добавленный код: интерфейс расширяет JpaRepository для CRUD операций с User (ООП: наследование; SOLID: LSP).

    Optional<User> findByUsername(String username); // добавленный код: кастомный метод для поиска по username (используется в UserDetailsService для аутентификации).
}