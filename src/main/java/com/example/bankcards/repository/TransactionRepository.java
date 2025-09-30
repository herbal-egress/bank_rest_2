package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// добавил: Репозиторий для работы с сущностью Transaction (без изменений, так как уже интерфейс)
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}