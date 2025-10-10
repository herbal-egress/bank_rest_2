package com.example.bankcards.exception;
public class CardAlreadyActiveException extends BankCardsException {
    public CardAlreadyActiveException(String message) {
        super(message);
    }
}