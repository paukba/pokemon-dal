package com.example.pokemon.service;

import com.example.pokemon.model.Card;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CardService {
    Card createCard(Card card) throws Exception;
    Optional<Card> getCard(int id) throws Exception;
    List<Card> listCards() throws Exception;
    boolean updateCard(Card card) throws Exception;
    boolean deleteCard(int id) throws Exception;

    /**
     * Convenience: update price using BigDecimal (business method).
     */
    boolean updateCardPrice(int cardId, BigDecimal newPrice) throws Exception;
}