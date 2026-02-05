package com.example.pokemon.dao;

import com.example.pokemon.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardDao {
    Card create(Card card) throws Exception;
    Optional<Card> findById(int cardId) throws Exception;
    List<Card> findAll() throws Exception;
    boolean update(Card card) throws Exception;
    boolean delete(int cardId) throws Exception;
}
