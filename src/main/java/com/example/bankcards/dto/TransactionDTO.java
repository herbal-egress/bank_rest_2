package com.example.bankcards.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    @NotNull(message = "ID карты-отправителя обязателен")  
    private Long fromCardId;
    @NotNull(message = "ID карты-получателя обязателен")  
    private Long toCardId;
    @NotNull(message = "Сумма обязательна")  
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")  
    private BigDecimal amount;
}