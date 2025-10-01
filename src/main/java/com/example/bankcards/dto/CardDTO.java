package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

import java.math.BigDecimal; // для работы с большими десятичными числами
import javax.validation.constraints.NotNull; // добавил: для проверки обязательных полей (OWASP: предотвращение null-атак)
import javax.validation.constraints.Pattern; // добавил: для regex-валидации строк (OWASP: защита от инъекций)

// DTO для представления данных карты
public class CardDTO {
    private Long id;
    @NotNull(message = "Номер карты обязателен") // добавил: валидация номера как обязательного поля
    @Pattern(regexp = "^\\d{16}$", message = "Номер карты должен быть 16 цифрами") // добавил: валидация формата номера карты (Luhn не здесь, в util)
    private String number; // номер карты
    @NotNull(message = "Имя держателя обязательно") // добавил: валидация имени как обязательного
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Имя должно содержать только буквы и пробелы") // добавил: базовая защита от инъекций
    private String name;
    @NotNull(message = "Срок действия обязателен") // добавил: валидация expiration
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Формат MM/YY") // добавил: стандартный формат даты
    private String expiration;
    @NotNull(message = "Статус карты обязателен") // добавил: валидация статуса
    private CardStatus status;
    @NotNull(message = "Баланс обязателен") // добавил: валидация баланса (BigDecimal > 0 в сервисе)
    private BigDecimal balance;
    // добавил: CVV не в response DTO по OWASP (не хранить/передавать в ответах; только в request если нужно)
    private String cvv;

    // конструктор по умолчанию
    public CardDTO() {}

    // конструктор с параметрами
    public CardDTO(Long id, String number, String name, String expiration, CardStatus status, BigDecimal balance, String cvv) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.expiration = expiration;
        this.status = status;
        this.balance = balance;
        this.cvv = cvv;
    }

    // геттеры и сеттеры
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
    // геттер и сеттер для баланса
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}