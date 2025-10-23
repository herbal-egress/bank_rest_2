package com.example.bankcards.service;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BankCardsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
@Service
public class UserCardServiceImpl implements UserCardService {
    private static final Logger logger = LoggerFactory.getLogger(UserCardServiceImpl.class);
    private final CardRepository cardRepository;
    private final SecurityUtil securityUtil; 
    private final CardMapper cardMapper;
    public UserCardServiceImpl(CardRepository cardRepository, SecurityUtil securityUtil, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.securityUtil = securityUtil;
        this.cardMapper = cardMapper;
    }
    @Override
    public Page<CardDTO> getUserCards(Pageable pageable) {
        try {
            Long userId = securityUtil.getCurrentUserId();
            logger.info("Получение всех карт для пользователя с ID: {}, pageable: {}", userId, pageable);
            Page<Card> cards = cardRepository.findByUserId(userId, pageable);
            logger.info("Найдено {} карт для пользователя с ID: {}", cards.getTotalElements(), userId);
            return cards.map(cardMapper::toDto);
        } catch (AccessDeniedException e) {
            logger.error("Ошибка доступа при получении карт пользователя: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при получении карт пользователя: {}", e.getMessage());
            throw new BankCardsException("Ошибка при получении карт пользователя", e);
        }
    }
    @Override
    public String requestBlockCard(Long cardId) {
        Long userId = securityUtil.getCurrentUserId();
        logger.info("Запрос на блокировку карты с ID: {} от пользователя с ID: {}", cardId, userId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
                });
        logger.info("Запрос на блокировку карты с ID {} успешно создан", cardId);
        return "Запрос на блокировку карты с ID " + cardId + " успешно отправлен";
    }
    @Override
    public CardDTO getCardBalance(Long cardId) { 
        Long userId = securityUtil.getCurrentUserId();
        logger.info("Получение баланса карты с ID: {} для пользователя с ID: {}", cardId, userId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена для пользователя с ID: {}", cardId, userId);
                    return new CardNotFoundException("Карта с ID " + cardId + " не найдена для пользователя с ID " + userId);
                });
        CardDTO cardDTO = cardMapper.toDto(card);
        logger.info("Баланс карты с ID {}: {}", cardId, cardDTO.getBalance());
        return cardDTO;
    }
}