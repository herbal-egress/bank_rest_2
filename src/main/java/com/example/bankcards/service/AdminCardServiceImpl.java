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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminCardServiceImpl implements AdminCardService {
    private static final Logger logger = LoggerFactory.getLogger(AdminCardServiceImpl.class);
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardFactory cardFactory;
    private final CardMapper cardMapper;

    public AdminCardServiceImpl(CardRepository cardRepository, UserRepository userRepository,
                                CardFactory cardFactory, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardFactory = cardFactory;
        this.cardMapper = cardMapper;
    }

    @Override
    public CardDTO createCard(Long userId, CardCreationDTO cardCreationDTO) {
        logger.info("Создание карты для пользователя с ID: {}, имя: {}", userId, cardCreationDTO.getName());
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new UserNotFoundException("Пользователь с ID " + userId + " не найден");
                });
        CardDTO cardDTO = cardFactory.createCard(cardCreationDTO.getName(), userId);
        CardValidator.validateCard(cardDTO);
        Card card = cardMapper.toEntity(cardDTO);
        card.setUser(new com.example.bankcards.entity.User());
        card.getUser().setId(userId);
        Card savedCard = cardRepository.save(card);
        logger.info("Карта успешно создана для пользователя с ID: {}, ID карты: {}", userId, savedCard.getId());
        return cardMapper.toDTO(savedCard);
    }

    @Override
    public Page<CardDTO> getAllCards(Long userId, String status, Pageable pageable) {
        logger.info("Получение карт: userId={}, status={}, pageable={}", userId, status, pageable);
        Page<Card> cards;
        if (userId != null && status != null) {
            cards = cardRepository.findByUserIdAndStatus(userId, CardStatus.valueOf(status), pageable);
        } else if (userId != null) {
            cards = cardRepository.findByUserId(userId, pageable);
        } else if (status != null) {
            cards = cardRepository.findByStatus(CardStatus.valueOf(status), pageable);
        } else {
            cards = cardRepository.findAll(pageable);
        }
        logger.info("Найдено {} карт", cards.getTotalElements());
        return cards.map(cardMapper::toDTO);
    }

    @Override
    public CardDTO blockCard(Long cardId) {
        logger.info("Блокировка карты с ID: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена");
                });
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        logger.info("Карта с ID {} успешно заблокирована", cardId);
        return cardMapper.toDTO(savedCard);
    }

    @Override
    public CardDTO activateCard(Long cardId) {
        logger.info("Активация карты с ID: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", cardId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена");
                });
        card.setStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);
        logger.info("Карта с ID {} успешно активирована", cardId);
        return cardMapper.toDTO(savedCard);
    }

    @Override
    public void deleteCard(Long cardId) {
        logger.info("Удаление карты с ID: {}", cardId);
        if (!cardRepository.existsById(cardId)) {
            logger.error("Карта с ID {} не найдена", cardId);
            throw new CardNotFoundException("Карта с ID " + cardId + " не найдена");
        }
        cardRepository.deleteById(cardId);
        logger.info("Карта с ID {} успешно удалена", cardId);
    }
}