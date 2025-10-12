package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class CardDTO {
    private Long id;
    @NotNull(message = "Поле id не может быть пустым")
    @Pattern(regexp = "^\\d{16}$", message = "Номер карты должен содержать 16 цифр")
    private String number;
    @NotNull(message = "Имя держателя карты не может быть пустым")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Имя держателя карты должно содержать только буквы")
    private String name;
    @NotNull(message = "Дата истечения срока действия не может быть пустой")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "ММ/ГГ")
    private String expiration;
    @NotNull(message = "Статус карты не может быть пустым")
    private CardStatus status;
    @NotNull(message = "Баланс не может быть пустым")
    private BigDecimal balance;
    // изменил: удалил поле cvv, так как не храним CVV

    public CardDTO() {
    }

    public CardDTO(Long id, String number, String name, String expiration, CardStatus status, BigDecimal balance) { // изменил: удалил параметр cvv из конструктора
        this.id = id;
        this.number = number;
        this.name = name;
        this.expiration = expiration;
        this.status = status;
        this.balance = balance;
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
        this.number = number;
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

    // изменил: удалил getter/setter для cvv
}