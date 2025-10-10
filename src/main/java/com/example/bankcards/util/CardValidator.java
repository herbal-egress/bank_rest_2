package com.example.bankcards.util;
import com.example.bankcards.dto.CardDTO;
import jakarta.validation.ValidationException;
public class CardValidator {
    private static final String CARD_NUMBER_PATTERN = "\\d{16}";
    private static final String NAME_PATTERN = "^[A-Za-z]+\\s[A-Za-z]+$";
    private static final String CCV_PATTERN = "\\d{3}";
    public static void validateCard(CardDTO cardDTO) {
        if (!cardDTO.getNumber().matches(CARD_NUMBER_PATTERN)) {
            throw new ValidationException("Номер карты должен содержать 16 цифр");
        }
        if (!cardDTO.getName().matches(NAME_PATTERN) || cardDTO.getName().length() > 50) {
            throw new ValidationException("Имя владельца должно состоять из двух слов, латиница, до 50 символов");
        }
        if (!cardDTO.getCvv().matches(CCV_PATTERN)) {
            throw new ValidationException("CCV должен содержать 3 цифры");
        }
        if (cardDTO.getBalance().scale() > 2) {
            throw new ValidationException("Баланс должен иметь не более 2 знаков после запятой");
        }
    }
}