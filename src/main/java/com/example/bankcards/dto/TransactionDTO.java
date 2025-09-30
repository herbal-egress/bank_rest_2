package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionStatus;

import java.time.LocalDateTime;

// изменил: Заменил fromCardId и toCardId на fromCard и toCard типа CardDTO
public class TransactionDTO {
    private Long id;
    private CardDTO fromCard; // изменил: Поле для исходной карты (CardDTO)
    private CardDTO toCard; // изменил: Поле для целевой карты (CardDTO)
    private double amount;
    private LocalDateTime timestamp;
    private TransactionStatus status;

    // добавил: Конструктор по умолчанию
    public TransactionDTO() {}

    // изменил: Конструктор теперь принимает CardDTO вместо Long
    public TransactionDTO(Long id, CardDTO fromCard, CardDTO toCard, double amount, LocalDateTime timestamp, TransactionStatus status) {
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
    // изменил: Геттер и сеттер для fromCard
    public CardDTO getFromCard() { return fromCard; }
    public void setFromCard(CardDTO fromCard) { this.fromCard = fromCard; }
    // изменил: Геттер и сеттер для toCard
    public CardDTO getToCard() { return toCard; }
    public void setToCard(CardDTO toCard) { this.toCard = toCard; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}