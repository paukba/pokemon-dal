package com.example.pokemon.dao;

import com.example.pokemon.model.Trade;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Trade records.
 *
 * Provides the usual DAO methods (that open/close their own connections)
 * and overloads that accept a java.sql.Connection. The connection-accepting
 * overloads are for use by service-layer code that manages transactions
 * (single Connection used across multiple DAO calls).
 */
public interface TradeDao {
    // Existing methods (manage their own connections inside implementations)
    Trade create(Trade trade) throws Exception;
    Optional<Trade> findById(int id) throws Exception;
    List<Trade> findAll() throws Exception;
    boolean update(Trade trade) throws Exception;
    boolean delete(int id) throws Exception;

    // ---- New connection-accepting overloads for transactional usage ----
    Trade create(Trade trade, Connection conn) throws Exception;
    Optional<Trade> findById(int id, Connection conn) throws Exception;
    List<Trade> findAll(Connection conn) throws Exception;
    boolean update(Trade trade, Connection conn) throws Exception;
    boolean delete(int id, Connection conn) throws Exception;
}