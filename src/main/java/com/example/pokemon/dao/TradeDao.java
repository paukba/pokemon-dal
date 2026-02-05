package com.example.pokemon.dao;

import com.example.pokemon.model.Trade;

import java.util.List;
import java.util.Optional;

public interface TradeDao {
    Trade create(Trade t) throws Exception;
    Optional<Trade> findById(int tradeId) throws Exception;
    List<Trade> findAll() throws Exception;
    List<Trade> findByOwnerId(int ownerId) throws Exception; // finds trades involving owner (from or to)
    boolean update(Trade t) throws Exception;
    boolean delete(int tradeId) throws Exception;
}
