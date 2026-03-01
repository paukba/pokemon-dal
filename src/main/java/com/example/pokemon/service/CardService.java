package com.example.pokemon.service;

import com.example.pokemon.model.Card;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Card service interface. updateCardPrice uses BigDecimal to match model type.
 */
public interface CardService {
    Card createCard(Card card) throws Exception;
    Optional<Card> getCard(int id) throws Exception;
    List<Card> listCards() throws Exception;

    /**
     * Update the market value (price) of a card.
     * Use BigDecimal because Card.marketValueUsd in your project uses BigDecimal.
     */
    boolean updateCardPrice(int cardId, BigDecimal newPrice) throws Exception;
}