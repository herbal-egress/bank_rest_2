package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal; // изменил: Добавлен импорт для BigDecimal
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

// изменил: Обновил для работы с BigDecimal
@Service
public class AdminCardServiceImpl implements AdminCardService {
    private static final Logger logger = LoggerFactory.getLogger(AdminCardServiceImpl.class);
    private static final String CARD_NUMBER_PREFIX = "1234";
    private static final DateTimeFormatter EXPIRATION_FORMATTER = DateTimeFormatter.ofPattern("MM-yy");

    private final CardRepository cardRepository;
    private final EncryptionUtil encryptionUtil;

    @Autowired
    // добавил: Конструктор для внедрения зависимостей
    public AdminCardServiceImpl(CardRepository cardRepository, EncryptionUtil encryptionUtil) {
        this.cardRepository = cardRepository;
        this.encryptionUtil = encryptionUtil;
    }

    // изменил: Используем BigDecimal для balance
    @Override
    public Card createCard(Long userId, String name) {
        logger.info("Создание карты для пользователя с ID: {}", userId);

        // добавил: Валидация имени владельца
        if (!isValidName(name)) {
            logger.error("Недопустимое имя владельца карты: {}", name);
            throw new IllegalArgumentException("Имя владельца должно состоять из двух слов, только латиница, до 50 символов");
        }

        Card card = new Card();
        card.setId(userId);
        card.setName(name);
        card.setNumber(generateCardNumber());
        card.setCvv(generateCcv());
        card.setExpiration(generateExpirationDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO); // изменил: Используем BigDecimal.ZERO

        // добавил: Шифрование номера карты перед сохранением
        String encryptedNumber = encryptionUtil.encrypt(card.getNumber());
        card.setNumber(encryptedNumber);

        Card savedCard = cardRepository.save(card);
        logger.info("Карта успешно создана для пользователя с ID: {}", userId);
        return savedCard;
    }

    // добавил: Реализация блокировки карты
    @Override
    public void blockCard(Long cardId) {
        logger.info("Блокировка карты с ID: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new IllegalArgumentException("Карта не найдена");
                });
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        logger.info("Карта с ID {} успешно заблокирована", cardId);
    }

    // добавил: Реализация активации карты
    @Override
    public void activateCard(Long cardId) {
        logger.info("Активация карты с ID: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new IllegalArgumentException("Карта не найдена");
                });
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        logger.info("Карта с ID {} успешно активирована", cardId);
    }

    // добавил: Реализация удаления карты
    @Override
    public void deleteCard(Long cardId) {
        logger.info("Удаление карты с ID: {}", cardId);
        if (!cardRepository.existsById(cardId)) {
            logger.error("Карта с ID {} не найдена", cardId);
            throw new IllegalArgumentException("Карта не найдена");
        }
        cardRepository.deleteById(cardId);
        logger.info("Карта с ID {} успешно удалена", cardId);
    }

    // добавил: Генерация номера карты
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder number = new StringBuilder(CARD_NUMBER_PREFIX);
        for (int i = 0; i < 12; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }

    // добавил: Генерация CCV
    private String generateCcv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    // добавил: Генерация срока действия (текущая дата + 5 лет)
    private String generateExpirationDate() {
        LocalDate expirationDate = LocalDate.now().plusYears(5);
        return expirationDate.format(EXPIRATION_FORMATTER);
    }

    // добавил: Валидация имени владельца
    private boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-Z]+\\s[a-zA-Z]+$") && name.length() <= 50;
    }
}