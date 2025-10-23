package com.example.bankcards.util;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.CardStatus;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
@Component
public class CardFactory {
    private static final String CARD_PREFIX = "1234";
    private static final Random RANDOM = new Random();
    public CardDTO createCard(String name, Long userId) {
        CardDTO card = new CardDTO();
        card.setNumber(CARD_PREFIX + String.format("%012d", RANDOM.nextInt(1000000000)));
        card.setName(name);
        card.setExpiration(LocalDate.now().plusYears(5).format(DateTimeFormatter.ofPattern("MM-yy")));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(CardStatus.ACTIVE);
        return card;
    }
}