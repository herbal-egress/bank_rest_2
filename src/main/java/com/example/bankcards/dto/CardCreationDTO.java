package com.example.bankcards.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCreationDTO {
    @NotBlank(message = "Имя владельца не может быть пустым")
    @Size(max = 50, message = "Имя владельца не должно превышать 50 символов")
    @Pattern(regexp = "^[A-Za-z]+\\s[A-Za-z]+$", message = "Имя владельца должно состоять из двух слов на латинице")
    private String name;
}