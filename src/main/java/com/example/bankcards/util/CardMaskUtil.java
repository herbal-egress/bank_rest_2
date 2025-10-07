// FILE: src/main/java/com/example/bankcards/util/CardMaskUtil.java
package com.example.bankcards.util;

import org.springframework.stereotype.Component;

/**
 * Утилита для маскирования номеров карт в соответствии с OWASP Security
 */
@Component
public class CardMaskUtil {

    private static final String MASK_PATTERN = "**** **** **** ";

    /**
     * Маскирует номер карты, оставляя только последние 4 цифры
     * добавил: Защита от показа полного номера карты в ответах API
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }
        return MASK_PATTERN + cardNumber.substring(12);
    }
}