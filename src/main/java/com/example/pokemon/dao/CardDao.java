package com.example.pokemon.dao;

import com.example.pokemon.model.Card;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * CardDao - data access operations for Card.
 *
 * Note: this interface exposes two flavors of methods:
 *  - convenience methods that manage their own Connection (create, findById, findAll, update, delete)
 *  - variants that accept a Connection so service layer can run multiple DAO calls within a single transaction.
 */
public interface CardDao {
    // convenience (connection-managed) methods
    Card create(Card card) throws Exception;
    Optional<Card> findById(int id) throws Exception;
    List<Card> findAll() throws Exception;
    boolean update(Card card) throws Exception;
    boolean delete(int id) throws Exception;

    // connection-taking variants for transactional usage
    Card create(Card card, Connection c) throws Exception;
    Optional<Card> findById(int id, Connection c) throws Exception;
    List<Card> findAll(Connection c) throws Exception;
    boolean update(Card card, Connection c) throws Exception;
    boolean delete(int id, Connection c) throws Exception;
}