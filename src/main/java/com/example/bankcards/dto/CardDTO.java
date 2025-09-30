package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

import java.math.BigDecimal; // изменил: Добавлен импорт для BigDecimal

// изменил: Обновил balance на BigDecimal
public class CardDTO {
    private Long id;
    private String number; // Маскированный номер карты
    private String name;
    private String expiration;
    private CardStatus status;
    // изменил: Тип balance изменён на BigDecimal
    private BigDecimal balance;
    private String cvv;

    // добавил: Конструктор по умолчанию
    public CardDTO() {}

    // изменил: Конструктор теперь принимает BigDecimal для balance
    public CardDTO(Long id, String number, String name, String expiration, CardStatus status, BigDecimal balance, String cvv) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.expiration = expiration;
        this.status = status;
        this.balance = balance;
        this.cvv = cvv;
    }

    // добавил: Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getExpiration() { return expiration; }
    public void setExpiration(String expiration) { this.expiration = expiration; }
    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
    // изменил: Геттер и сеттер для BigDecimal
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}