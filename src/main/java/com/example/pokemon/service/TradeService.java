package com.example.pokemon.service;

import com.example.pokemon.model.Trade;
import java.util.List;
import java.util.Optional;

public interface TradeService {
    Trade proposeTrade(int fromOwnerId, int toOwnerId, int cardId, int qty, String notes) throws Exception;
    ServiceResult acceptTrade(int tradeId) throws Exception;
    Optional<Trade> getTrade(int tradeId) throws Exception;
    List<Trade> listTrades() throws Exception;
}