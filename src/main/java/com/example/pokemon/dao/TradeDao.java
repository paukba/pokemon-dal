package com.example.pokemon.dao;

import com.example.pokemon.model.Trade;

import java.util.List;
import java.util.Optional;

public interface TradeDao {
    Trade create(Trade trade) throws Exception;
    Optional<Trade> findById(int id) throws Exception;
    List<Trade> findAll() throws Exception;
    boolean update(Trade trade) throws Exception;
    boolean delete(int id) throws Exception;
}