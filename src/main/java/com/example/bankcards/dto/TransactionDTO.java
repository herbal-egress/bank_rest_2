package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    @NotNull(message = "Карта-отправитель обязательна")
    private CardDTO fromCard;
    @NotNull(message = "Карта-получатель обязательна")
    private CardDTO toCard;
    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть положительной")
    private BigDecimal amount;
    private LocalDateTime timestamp;
    @NotNull(message = "Статус транзакции обязателен")
    private TransactionStatus status;

    public TransactionDTO() {
    }

    public TransactionDTO(Long id, CardDTO fromCard, CardDTO toCard, BigDecimal amount, LocalDateTime timestamp, TransactionStatus status) {
        this.id = id;
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CardDTO getFromCard() {
        return fromCard;
    }

    public void setFromCard(CardDTO fromCard) {
        this.fromCard = fromCard;
    }

    public CardDTO getToCard() {
        return toCard;
    }

    public void setToCard(CardDTO toCard) {
        this.toCard = toCard;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}