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
    private final SecurityUtil securityUtil;

    public AdminCardServiceImpl(CardRepository cardRepository, UserRepository userRepository,
                                CardFactory cardFactory, CardMapper cardMapper, SecurityUtil securityUtil) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardFactory = cardFactory;
        this.cardMapper = cardMapper;
        this.securityUtil = securityUtil;
    }

    @Override
    public CardDTO createCard(Long userId, CardCreationDTO cardCreationDTO) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} создает карту для пользователя {}: {}",
                adminUsername, userId, cardCreationDTO.getName());
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден", userId);
                    return new UserNotFoundException("Пользователь " + userId + " не найден");
                });
        CardDTO cardDTO = cardFactory.createCard(cardCreationDTO.getName(), userId);
        CardValidator.validateCard(cardDTO);
        Card card = cardMapper.toEntity(cardDTO);
        card.setUser(new com.example.bankcards.entity.User());
        card.getUser().setId(userId);
        Card savedCard = cardRepository.save(card);
        logger.info("Администратор {} создал карту для пользователя {} с ID: {}",
                adminUsername, userId, savedCard.getId());
        return cardMapper.toDto(savedCard);
    }

    @Override
    public Page<CardDTO> getAllCards(Pageable pageable) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} запрашивает все карты, pageable={}", adminUsername, pageable);
        Page<Card> cards = cardRepository.findAll(pageable);
        logger.info("Администратор {} получил {} карт", adminUsername, cards.getTotalElements());
        return cards.map(cardMapper::toDto);
    }

    @Override
    @Transactional
    public CardDTO blockCard(Long cardId) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} блокирует карту {}", adminUsername, cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта {} не найдена", cardId);
                    return new CardNotFoundException("Карта " + cardId + " не найдена");
                });
        if (card.getStatus() == CardStatus.BLOCKED) {
            logger.warn("Администратор {} пытается заблокировать уже заблокированную карту {}",
                    adminUsername, cardId);
            return cardMapper.toDto(card);
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            logger.warn("Администратор {} пытается заблокировать карту {} со статусом {}",
                    adminUsername, cardId, card.getStatus());
            return cardMapper.toDto(card);
        }
        try {
            card.setStatus(CardStatus.BLOCKED);
            logger.debug("Администратор {} устанавливает статус карты {} в BLOCKED",
                    adminUsername, cardId, CardStatus.BLOCKED);
            Card savedCard = cardRepository.save(card);
            logger.info("Администратор {} успешно заблокировал карту {}", adminUsername, cardId);
            return cardMapper.toDto(savedCard);
        } catch (Exception e) {
            logger.error("Ошибка при блокировке карты {} администратором {}: {} {}",
                    cardId, adminUsername, e.getMessage(), e);
            return cardMapper.toDto(card);
        }
    }

    @Override
    @Transactional
    public CardDTO activateCard(Long cardId) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} активирует карту {}", adminUsername, cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Карта {} не найдена", cardId);
                    return new CardNotFoundException("Карта " + cardId + " не найдена");
                });
        if (card.getStatus() == CardStatus.ACTIVE) {
            logger.warn("Администратор {} пытается активировать уже активную карту {}",
                    adminUsername, cardId);
            return cardMapper.toDto(card);
        }
        if (card.getStatus() != CardStatus.BLOCKED) {
            logger.warn("Администратор {} пытается активировать карту {} со статусом {}",
                    adminUsername, cardId, card.getStatus());
            return cardMapper.toDto(card);
        }
        try {
            card.setStatus(CardStatus.ACTIVE);
            Card savedCard = cardRepository.save(card);
            logger.info("Администратор {} успешно активировал карту {}", adminUsername, cardId);
            return cardMapper.toDto(savedCard);
        } catch (Exception e) {
            logger.error("Ошибка при активации карты {} администратором {}: {} {}",
                    cardId, adminUsername, e.getMessage(), e);
            return cardMapper.toDto(card);
        }
    }

    @Override
    public void deleteCard(Long cardId) {
        securityUtil.validateAdminAccess();
        String adminUsername = securityUtil.getCurrentUsername();
        logger.info("Администратор {} удаляет карту {}", adminUsername, cardId);
        if (!cardRepository.existsById(cardId)) {
            logger.error("Карта {} не найдена", cardId);
            throw new CardNotFoundException("Карта " + cardId + " не найдена");
        }
        cardRepository.deleteById(cardId);
        logger.info("Администратор {} успешно удалил карту {}", adminUsername, cardId);
    }
}