package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// добавил: Репозиторий для работы с сущностью Card (без изменений, так как уже интерфейс)
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    // добавил: Поиск карт по ID пользователя с пагинацией
    Page<Card> findByUserId(Long userId, Pageable pageable);
    // Добавлено: Поиск карт по userId и status с пагинацией
    Page<Card> findByUserIdAndStatus(Long userId, CardStatus status, Pageable pageable);
    // Добавлено: Поиск карт по userId и status с пагинацией
    Page<Card> findByStatus(CardStatus status, Pageable pageable);
    // добавил: Поиск карты по ID и ID пользователя
    Optional<Card> findByIdAndUserId(Long id, Long userId);
}