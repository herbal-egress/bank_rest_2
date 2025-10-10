package com.example.bankcards.service;
import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface AdminCardService {
    CardDTO createCard(Long userId, CardCreationDTO cardCreationDTO);
    Page<CardDTO> getAllCards(Pageable pageable);
    CardDTO blockCard(Long cardId);
    CardDTO activateCard(Long cardId);
    void deleteCard(Long cardId);
}