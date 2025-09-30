package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionStatus;

import java.time.LocalDateTime;

// добавил: DTO для передачи данных о транзакции через API (без изменений)
public class TransactionDTO {
    private Long id;
    private Long fromCardId;
    private Long toCardId;
    private double amount;
    private LocalDateTime timestamp;
    private TransactionStatus status;

    // добавил: Конструктор по умолчанию
    public TransactionDTO() {}

    // добавил: Полный конструктор
    public TransactionDTO(Long id, Long fromCardId, Long toCardId, double amount, LocalDateTime timestamp, TransactionStatus status) {
        this.id = id;
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    // добавил: Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFromCardId() { return fromCardId; }
    public void setFromCardId(Long fromCardId) { this.fromCardId = fromCardId; }
    public Long getToCardId() { return toCardId; }
    public void setToCardId(Long toCardId) { this.toCardId = toCardId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}