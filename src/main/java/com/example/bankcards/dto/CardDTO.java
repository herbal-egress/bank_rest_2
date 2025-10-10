package com.example.bankcards.dto;
import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
public class CardDTO {
    private Long id;
    @NotNull(message = "Номер карты обязателен")
    @Pattern(regexp = "^\\d{16}$", message = "Номер карты должен быть 16 цифрами")
    private String number;
    @NotNull(message = "Имя держателя обязательно")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Имя должно содержать только буквы и пробелы")
    private String name;
    @NotNull(message = "Срок действия обязателен")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Формат MM/YY")
    private String expiration;
    @NotNull(message = "Статус карты обязателен")
    private CardStatus status;
    @NotNull(message = "Баланс обязателен")
    private BigDecimal balance;
    private String cvv;
    public CardDTO() {
    }
    public CardDTO(Long id, String number, String name, String expiration, CardStatus status, BigDecimal balance, String cvv) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.expiration = expiration;
        this.status = status;
        this.balance = balance;
        this.cvv = cvv;
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
    public String getCvv() {
        return cvv;
    }
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}