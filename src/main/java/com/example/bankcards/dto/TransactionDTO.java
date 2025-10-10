package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data  // добавил: Lombok для геттеров/сеттеров, минимизация boilerplate.
public class TransactionDTO {
    @NotNull(message = "ID карты-отправителя обязателен")  // добавил: валидация, OWASP: input validation.
    private Long fromCardId;

    @NotNull(message = "ID карты-получателя обязателен")  // добавил: валидация.
    private Long toCardId;

    @NotNull(message = "Сумма обязательна")  // добавил: валидация.
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")  // добавил: минимальная сумма.
    private BigDecimal amount;

    // добавил: только необходимые поля, REST: минимальная передача данных.
    // удалены: id, fromCard, toCard, timestamp, status — не нужны для запроса, генерируются сервером.
}