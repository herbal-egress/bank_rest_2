package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Изменено: Обновлен интерфейс для соответствия эндпоинтам
public interface AdminCardService {
    // Изменено: Принимает CardCreationDTO, возвращает CardDTO
    CardDTO createCard(Long userId, CardCreationDTO cardCreationDTO);
    // Добавлено: Поддержка пагинации и фильтрации
    Page<CardDTO> getAllCards(Long userId, String status, Pageable pageable);
    // Изменено: Возвращает CardDTO
    CardDTO blockCard(Long cardId);
    // Изменено: Возвращает CardDTO
    CardDTO activateCard(Long cardId);
    void deleteCard(Long cardId);
}