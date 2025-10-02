package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByUserId(Long userId, Pageable pageable);

    Page<Card> findByUserIdAndStatus(Long userId, CardStatus status, Pageable pageable);

    Page<Card> findByStatus(CardStatus status, Pageable pageable);

    Optional<Card> findByIdAndUserId(Long id, Long userId);
}