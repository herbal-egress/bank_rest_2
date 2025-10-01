package com.example.bankcards.exception;

/**
 * Исключение для случая недостаточного баланса.
 */
public class InsufficientBalanceException extends BankCardsException {
    public InsufficientBalanceException(Long id) {
        super("Недостаточно средств на карте с ID " + id);
    }
}