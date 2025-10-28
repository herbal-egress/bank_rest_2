package com.example.bankcards.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
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
    @NotNull
    @Positive(message = "Сумма транзакции должна быть положительной")
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;
    public Transaction() {
    }
    public Transaction(Card fromCard, Card toCard, BigDecimal amount, LocalDateTime timestamp, TransactionStatus status) {
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