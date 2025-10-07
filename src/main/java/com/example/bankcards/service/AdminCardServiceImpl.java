package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardFactory;
import com.example.bankcards.util.CardValidator;
import com.example.bankcards.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminCardServiceImpl implements AdminCardService {
    private static final Logger logger = LoggerFactory.getLogger(AdminCardServiceImpl.class);
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardFactory cardFactory;
    private final CardMapper cardMapper;
    private final SecurityUtil securityUtil; // добавил: использование SecurityUtil для логирования действий администратора

    // изменил: добавляю SecurityUtil в конструктор
    public AdminCardServiceImpl(CardRepository cardRepository, UserRepository userRepository,
                                CardFactory cardFactory, CardMapper cardMapper, SecurityUtil securityUtil) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardFactory = cardFactory;
        this.cardMapper = cardMapper;
        this.securityUtil = securityUtil; // добавил: инициализация
    }

    @Override
    public CardDTO createCard(Long userId, CardCreationDTO cardCreationDTO) {
        // добавил: логирование действия администратора
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} создает карту для пользователя с ID: {}, имя: {}",
                adminUsername, userId, cardCreationDTO.getName());

        // добавил: проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new UserNotFoundException("Пользователь с ID " + userId + " не найден");
                });

        // добавил: создание карты через фабрику
        CardDTO cardDTO = cardFactory.createCard(cardCreationDTO.getName(), userId);
        CardValidator.validateCard(cardDTO);
        Card card = cardMapper.toEntity(cardDTO);
        card.setUser(new com.example.bankcards.entity.User());
        card.getUser().setId(userId);
        Card savedCard = cardRepository.save(card);

        logger.info("Администратор {} успешно создал карту для пользователя с ID: {}, ID карты: {}",
                adminUsername, userId, savedCard.getId());
        return cardMapper.toDTO(savedCard);
    }

    @Override
    public Page<CardDTO> getAllCards(Pageable pageable) {
        // добавил: логирование действия администратора
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} получает все карты: pageable={}", adminUsername, pageable);

        // изменил: Убрана фильтрация по userId, возвращаются все карты
        Page<Card> cards = cardRepository.findAll(pageable);
        logger.info("Администратор {} получил {} карт", adminUsername, cards.getTotalElements());
        return cards.map(cardMapper::toDTO);
    }

    @Override
    @Transactional
    public CardDTO blockCard(Long cardId) {
        // добавил: логирование действия администратора
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} блокирует карту с ID: {}", adminUsername, cardId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена");
                });

        // изменил: вместо исключения возвращаем карту с сообщением в логах
        if (card.getStatus() == CardStatus.BLOCKED) {
            logger.warn("Администратор {} попытался заблокировать уже заблокированную карту с ID: {}",
                    adminUsername, cardId);
            return cardMapper.toDTO(card);
        }

        // изменил: проверка статуса с информативным сообщением в логах
        if (card.getStatus() != CardStatus.ACTIVE) {
            logger.warn("Администратор {} не может заблокировать карту с ID {} со статусом: {}",
                    adminUsername, cardId, card.getStatus());
            // Возвращаем текущую карту без изменений
            return cardMapper.toDTO(card);
        }

        try {
            card.setStatus(CardStatus.BLOCKED);
            logger.debug("Администратор {} сохраняет карту с ID: {} со статусом: {}",
                    adminUsername, cardId, CardStatus.BLOCKED);

            Card savedCard = cardRepository.save(card);
            logger.info("Администратор {} успешно заблокировал карту с ID {}", adminUsername, cardId);
            return cardMapper.toDTO(savedCard);

        } catch (Exception e) {
            logger.error("Ошибка транзакции при блокировке карты с ID {} администратором {}: {}",
                    cardId, adminUsername, e.getMessage(), e);
            // Возвращаем исходную карту при ошибке
            return cardMapper.toDTO(card);
        }
    }

    @Override
    @Transactional
    public CardDTO activateCard(Long cardId) {
        // добавил: логирование действия администратора
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} активирует карту с ID: {}", adminUsername, cardId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена");
                });

        // изменил: вместо исключения возвращаем карту с сообщением в логах
        if (card.getStatus() == CardStatus.ACTIVE) {
            logger.warn("Администратор {} попытался активировать активную карту с ID: {}",
                    adminUsername, cardId);
            return cardMapper.toDTO(card);
        }

        // изменил: проверка статуса с информативным сообщением в логах
        if (card.getStatus() != CardStatus.BLOCKED) {
            logger.warn("Администратор {} не может активировать карту с ID {} со статусом: {}",
                    adminUsername, cardId, card.getStatus());
            // Возвращаем текущую карту без изменений
            return cardMapper.toDTO(card);
        }

        try {
            card.setStatus(CardStatus.ACTIVE);
            Card savedCard = cardRepository.save(card);
            logger.info("Администратор {} успешно активировал карту с ID {}", adminUsername, cardId);
            return cardMapper.toDTO(savedCard);

        } catch (Exception e) {
            logger.error("Ошибка транзакции при активации карты с ID {} администратором {}: {}",
                    cardId, adminUsername, e.getMessage(), e);
            // Возвращаем исходную карту при ошибке
            return cardMapper.toDTO(card);
        }
    }

    @Override
    public void deleteCard(Long cardId) {
        // добавил: логирование действия администратора
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} удаляет карту с ID: {}", adminUsername, cardId);

        if (!cardRepository.existsById(cardId)) {
            logger.error("Карта с ID: {} не найдена", cardId);
            throw new CardNotFoundException("Карта с ID " + cardId + " не найдена");
        }
        cardRepository.deleteById(cardId);
        logger.info("Администратор {} успешно удалил карту с ID {}", adminUsername, cardId);
    }
}