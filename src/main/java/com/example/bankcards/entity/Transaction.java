package com.example.bankcards.entity;

// изменил ИИ: Добавлен импорт для валидации @Positive.
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", schema = "bankrest")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;

    // изменил ИИ: Добавлена валидация @Positive для amount (ТЗ: сумма перевода должна быть положительной).
    @NotNull
    @Positive(message = "Сумма транзакции должна быть положительной")
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private double amount;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    public Transaction() {
    }

    public Transaction(Card fromCard, Card toCard, double amount, LocalDateTime timestamp, TransactionStatus status) {
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

    public Card getFromCard() {
        return fromCard;
    }

    public void setFromCard(Card fromCard) {
        this.fromCard = fromCard;
    }

    public Card getToCard() {
        return toCard;
    }

    public void setToCard(Card toCard) {
        this.toCard = toCard;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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