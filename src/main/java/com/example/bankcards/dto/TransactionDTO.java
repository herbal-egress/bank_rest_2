package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionStatus;

import java.math.BigDecimal; // для работы с BigDecimal
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull; // добавил: для проверки обязательных полей (OWASP)
import javax.validation.constraints.Pattern; // добавил: для regex-валидации
import javax.validation.constraints.DecimalMin; // добавил: для проверки минимального значения (amount > 0)

// DTO для представления транзакции, содержит поля amount в формате BigDecimal
public class TransactionDTO {
    private Long id;
    @NotNull(message = "Карта-отправитель обязательна") // добавил: валидация fromCard
    private CardDTO fromCard;
    @NotNull(message = "Карта-получатель обязательна") // добавил: валидация toCard
    private CardDTO toCard;
    @NotNull(message = "Сумма обязательна") // добавил: валидация amount
    @DecimalMin(value = "0.01", message = "Сумма должна быть положительной") // добавил: минимальная сумма > 0 (OWASP: предотвращение отрицательных значений)
    private BigDecimal amount;
    // добавил: timestamp генерируется автоматически в сервисе, но валидируем если передаётся
    private LocalDateTime timestamp;
    @NotNull(message = "Статус транзакции обязателен") // добавил: валидация статуса
    private TransactionStatus status;

    // конструктор по умолчанию
    public TransactionDTO() {}

    // конструктор с параметрами
    public TransactionDTO(Long id, CardDTO fromCard, CardDTO toCard, BigDecimal amount, LocalDateTime timestamp, TransactionStatus status) {
        this.id = id;
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    // геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CardDTO getFromCard() { return fromCard; }
    public void setFromCard(CardDTO fromCard) { this.fromCard = fromCard; }
    public CardDTO getToCard() { return toCard; }
    public void setToCard(CardDTO toCard) { this.toCard = toCard; }
    // геттер и сеттер для amount в формате BigDecimal
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}