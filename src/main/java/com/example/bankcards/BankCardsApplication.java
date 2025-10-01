package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // добавил: для активации @Scheduled

// Главный класс приложения
@SpringBootApplication
@EnableScheduling // добавил: включение планировщика задач для ротации ключей
public class BankCardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankCardsApplication.class, args);
    }
}