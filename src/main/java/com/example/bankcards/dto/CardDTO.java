package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

// добавил: DTO для передачи данных о карте через API (без изменений)
public class CardDTO {
    private Long id;
    private String number; // Маскированный номер карты
    private String name;
    private String expiration;
    private CardStatus status;
    private double balance;
    private String ccv;

    // добавил: Конструктор по умолчанию
    public CardDTO() {}

    // добавил: Полный конструктор
    public CardDTO(Long id, String number, String name, String expiration, CardStatus status, double balance, String ccv) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.expiration = expiration;
        this.status = status;
        this.balance = balance;
        this.ccv = ccv;
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
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getCcv() { return ccv; }
    public void setCcv(String ccv) { this.ccv = ccv; }
}