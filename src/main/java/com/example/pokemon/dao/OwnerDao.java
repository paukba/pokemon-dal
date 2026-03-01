package com.example.pokemon.dao;

import com.example.pokemon.model.Owner;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Owner records.
 *
 * Includes connection-accepting overloads so service methods can reuse a single
 * Connection for transactions when performing multi-step operations.
 */
public interface OwnerDao {
    // Existing methods
    Owner create(Owner owner) throws Exception;
    Optional<Owner> findById(int id) throws Exception;
    List<Owner> findAll() throws Exception;
    boolean update(Owner owner) throws Exception;
    boolean delete(int id) throws Exception;

    // ---- New connection-accepting overloads for transactional usage ----
    Optional<Owner> findById(int id, Connection conn) throws Exception;
    boolean update(Owner owner, Connection conn) throws Exception;
}