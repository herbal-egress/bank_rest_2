package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
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
//    @NotNull(message = "Баланс не может быть пустым")
//    private BigDecimal balance;


}