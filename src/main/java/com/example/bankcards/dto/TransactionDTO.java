package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionStatus;

import java.math.BigDecimal; // изменил: Добавлен импорт для BigDecimal
import java.time.LocalDateTime;

// изменил: Обновил amount на BigDecimal
public class TransactionDTO {
    private Long id;
    private CardDTO fromCard;
    private CardDTO toCard;
    // изменил: Тип amount изменён на BigDecimal
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionStatus status;

    // добавил: Конструктор по умолчанию
    public TransactionDTO() {}

    // изменил: Конструктор теперь принимает BigDecimal для amount
    public TransactionDTO(Long id, CardDTO fromCard, CardDTO toCard, BigDecimal amount, LocalDateTime timestamp, TransactionStatus status) {
        this.id = id;
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    // добавил: Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CardDTO getFromCard() { return fromCard; }
    public void setFromCard(CardDTO fromCard) { this.fromCard = fromCard; }
    public CardDTO getToCard() { return toCard; }
    public void setToCard(CardDTO toCard) { this.toCard = toCard; }
    // изменил: Геттер и сеттер для BigDecimal
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}