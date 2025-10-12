package com.example.bankcards.util;
import com.example.bankcards.dto.CardDTO;
import jakarta.validation.ValidationException;
public class CardValidator {
    private static final String CARD_NUMBER_PATTERN = "\\d{16}";
    private static final String NAME_PATTERN = "^[A-Za-z]+\\s[A-Za-z]+$";
    // изменил: удалил CCV_PATTERN, так как CVV удален
    public static void validateCard(CardDTO cardDTO) {
        if (!cardDTO.getNumber().matches(CARD_NUMBER_PATTERN)) {
            throw new ValidationException("Неверный формат номера карты. Должен содержать 16 цифр");
        }
        if (!cardDTO.getName().matches(NAME_PATTERN) || cardDTO.getName().length() > 50) {
            throw new ValidationException("Неверный формат имени. Должно быть 2 слова, не более 50 символов");
        }
        // изменил: удалил валидацию CVV
        if (cardDTO.getBalance().scale() > 2) {
            throw new ValidationException("Баланс должен содержать не более 2 знаков после запятой");
        }
    }
}