package com.example.pokemon.dao;

import com.example.pokemon.model.CollectionEntry;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for collection entries.
 *
 * Note: This interface provides the usual DAO methods (that open their own connection)
 * as well as overloads that accept a java.sql.Connection. The connection-accepting
 * overloads are intended for use by service-layer methods that manage JDBC transactions
 * (setAutoCommit(false)/commit/rollback) and want to perform multiple DAO operations
 * atomically with a single Connection.
 */
public interface CollectionDao {
    // Existing methods (manage their own connections inside implementations)
    CollectionEntry create(CollectionEntry entry) throws Exception;
    Optional<CollectionEntry> findById(int id) throws Exception;
    List<CollectionEntry> findByOwnerId(int ownerId) throws Exception;
    List<CollectionEntry> findAll() throws Exception;
    boolean update(CollectionEntry entry) throws Exception;
    boolean delete(int id) throws Exception;

    // ---- New connection-accepting overloads for transactional usage ----
    CollectionEntry create(CollectionEntry entry, Connection conn) throws Exception;
    Optional<CollectionEntry> findById(int id, Connection conn) throws Exception;
    List<CollectionEntry> findByOwnerId(int ownerId, Connection conn) throws Exception;
    List<CollectionEntry> findAll(Connection conn) throws Exception;
    boolean update(CollectionEntry entry, Connection conn) throws Exception;
    boolean delete(int id, Connection conn) throws Exception;
}