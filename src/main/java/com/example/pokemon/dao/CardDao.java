package com.example.pokemon.dao;

import com.example.pokemon.model.Card;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Card records.
 *
 * Adds overloads that accept a Connection so callers (service layer) can run several DAO operations
 * within the same JDBC transaction (Connection).
 */
public interface CardDao {
    // Existing methods
    Card create(Card card) throws Exception;
    Optional<Card> findById(int id) throws Exception;
    List<Card> findAll() throws Exception;
    boolean update(Card card) throws Exception;
    boolean delete(int id) throws Exception;

    // ---- New connection-accepting overloads for transactional usage ----
    Optional<Card> findById(int id, Connection conn) throws Exception;
    List<Card> findAll(Connection conn) throws Exception;
    boolean update(Card card, Connection conn) throws Exception;
}