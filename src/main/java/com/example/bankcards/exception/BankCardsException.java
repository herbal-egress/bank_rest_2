package com.example.bankcards.exception;

/**
 * Базовое исключение для приложения.
 */
public class BankCardsException extends RuntimeException {
    public BankCardsException(String message) {
        super(message);
    }
}