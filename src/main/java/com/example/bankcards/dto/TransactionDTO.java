package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для перевода средств между картами")
public class TransactionDTO {
    @NotNull(message = "ID карты-отправителя обязателен")
    @Schema(description = "ID карты-отправителя", example = "1", required = true)
    private Long fromCardId;

    @NotNull(message = "ID карты-получателя обязателен")
    @Schema(description = "ID карты-получателя", example = "2", required = true)
    private Long toCardId;

    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    @Schema(description = "Сумма перевода", example = "100.00", required = true, minimum = "0.01")
    private BigDecimal amount;
}