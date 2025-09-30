package com.example.bankcards.entity;
// добавленный код: Импорты для JPA (Entity, Id, GeneratedValue, Column, JoinColumn, ManyToOne для связей с Card).
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
// добавленный код: Без Lombok.
// добавленный код: Enum для status (Success, Failed).
enum TransactionStatus {
    SUCCESS,
    FAILED
}
// добавленный код: @Entity и @Table.
@Entity
@Table(name = "transactions", schema = "bankrest")
public class Transaction {
    // добавленный код: id - автоинкремент.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // добавленный код: fromCardId - связь с Card (ManyToOne, внешний ключ).
    @ManyToOne
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;
    // добавленный код: toCardId - аналогично.
    @ManyToOne
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;
    // добавленный код: amount - BigDecimal, scale=2.
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    // добавленный код: timestamp - LocalDateTime, для времени транзакции.
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    // добавленный код: status - Enum.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;
    // добавленный код: Конструктор по умолчанию.
    public Transaction() {
    }
    // добавленный код: Полный конструктор.
    public Transaction(Card fromCard, Card toCard, BigDecimal amount, LocalDateTime timestamp, TransactionStatus status) {
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }
    // добавленный код: Getter/Setter.
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