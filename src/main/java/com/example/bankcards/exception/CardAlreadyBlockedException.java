package com.example.bankcards.exception;
public class CardAlreadyBlockedException extends BankCardsException {
    public CardAlreadyBlockedException(String message) {
        super(message);
    }
}