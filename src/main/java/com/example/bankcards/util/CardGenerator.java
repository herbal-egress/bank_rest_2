package com.example.bankcards.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

// добавил: Класс для генерации данных карты
public class CardGenerator {
    private static final String CARD_NUMBER_PREFIX = "1234";
    private static final DateTimeFormatter EXPIRATION_FORMATTER = DateTimeFormatter.ofPattern("MM-yy");

    // добавил: Генерация номера карты
    public static String generateCardNumber() {
        Random random = new Random();
        StringBuilder number = new StringBuilder(CARD_NUMBER_PREFIX);
        for (int i = 0; i < 12; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }

    // добавил: Генерация CCV
    public static String generateCcv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    // добавил: Генерация срока действия (текущая дата + 5 лет)
    public static String generateExpirationDate() {
        LocalDate expirationDate = LocalDate.now().plusYears(5);
        return expirationDate.format(EXPIRATION_FORMATTER);
    }
}