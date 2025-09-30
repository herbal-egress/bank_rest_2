package com.example.bankcards.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// добавленный код: Enum для status (без изменений, сохранено для целостности).
enum CardStatus {
    ACTIVE,
    BLOCKED,
    EXPIRED
}

@Entity
@Table(name = "cards", schema = "bankrest")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 16, max = 16, message = "Номер карты должен состоять ровно из 16 цифр")
    @Column(name = "number", nullable = false)
    private String number;

    @NotNull
    @Size(max = 50, message = "Имя владельца не более 50 символов")
    @Pattern(regexp = "^[A-Za-z]+\\s[A-Za-z]+$", message = "Имя владельца: два слова латиницей, разделенные пробелом")
    @Column(name = "name", nullable = false)
    private String name;

    // изменил ИИ: Упростил валидацию для expiration: Разделил на @Size(min=5,max=5) для точной длины MM-YY и @Pattern для формата (01-12 для месяца, две цифры для года). Исправил экранирование \d.
    @NotNull
    @Size(min = 5, max = 5, message = "Срок действия должен быть в формате MM-YY")
    @Pattern(regexp = "^(0[1-9]|1[0-2])-\\d{2}$", message = "Срок действия: месяц 01-12, год две цифры")
    @Column(name = "expiration", nullable = false)
    private String expiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @NotNull
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    // изменил ИИ: Упростил валидацию для ccv: Исправил экранирование в @Pattern (\\d{3}); добавил @Size(min=3,max=3) для явной проверки длины, что улучшает читаемость.
    @NotNull
    @Size(min = 3, max = 3, message = "CCV должен состоять ровно из 3 цифр")
    @Pattern(regexp = "^\\d{3}$", message = "CCV: только цифры")
    @Column(name = "ccv", nullable = false)
    private String ccv;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Card() {
    }

    public Card(String number, String name, String expiration, CardStatus status, BigDecimal balance, String ccv, Long userId) {
        this.number = number;
        this.name = name;
        this.expiration = expiration;
        this.status = status;
        this.balance = balance;
        this.ccv = ccv;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number; // TODO: Шифрование через EncryptionUtil перед сохранением (OWASP: данные в покое).
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}