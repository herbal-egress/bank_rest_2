package com.example.bankcards.service;
import com.example.bankcards.dto.TransactionDTO;
public interface TransactionService {
    TransactionDTO transfer(TransactionDTO transactionDTO);
}