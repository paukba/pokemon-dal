package com.example.pokemon.service.impl;

import com.example.pokemon.dao.CardDao;
import com.example.pokemon.model.Card;
import com.example.pokemon.service.CardService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Card service implementation (simple validation + DAO delegation).
 */
public class CardServiceImpl implements CardService {
    private final CardDao cardDao;

    public CardServiceImpl(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    @Override
    public Card createCard(Card card) throws Exception {
        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        if (card.getName() == null || card.getName().trim().isEmpty())
            throw new IllegalArgumentException("Card name required");
        return cardDao.create(card);
    }

    @Override
    public Optional<Card> getCard(int id) throws Exception {
        return cardDao.findById(id);
    }

    @Override
    public List<Card> listCards() throws Exception {
        return cardDao.findAll();
    }

    @Override
    public boolean updateCard(Card card) throws Exception {
        if (card == null || card.getCardId() == null) throw new IllegalArgumentException("Card id required");
        return cardDao.update(card);
    }

    @Override
    public boolean deleteCard(int id) throws Exception {
        return cardDao.delete(id);
    }

    @Override
    public boolean updateCardPrice(int cardId, BigDecimal newPrice) throws Exception {
        if (newPrice != null && newPrice.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price cannot be negative.");
        Optional<Card> opt = cardDao.findById(cardId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Card not found: " + cardId);
        Card c = opt.get();
        c.setMarketValueUsd(newPrice);
        return cardDao.update(c);
    }
}