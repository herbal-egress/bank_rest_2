package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// добавленный код: Аннотация для активации Spring Boot.
@SpringBootApplication
public class BankcardsApplication {

    // добавленный код: Главный метод для запуска приложения.
    public static void main(String[] args) {
        SpringApplication.run(BankcardsApplication.class, args);
    }
}