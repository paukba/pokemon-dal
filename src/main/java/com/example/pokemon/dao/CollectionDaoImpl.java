package com.example.pokemon.dao;

import com.example.pokemon.model.CollectionEntry;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Collection DAO implementation.
 *
 * The no-arg methods open a connection and delegate to the connection-accepting overloads.
 * The connection-accepting overloads use the provided Connection and must NOT close it.
 */
public class CollectionDaoImpl implements CollectionDao {

    // -----------------------------------------------------------
    // No-arg methods (open their own Connection, delegate to overloads)
    // -----------------------------------------------------------

    @Override
    public CollectionEntry create(CollectionEntry entry) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return create(entry, conn);
        }
    }

    @Override
    public Optional<CollectionEntry> findById(int id) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return findById(id, conn);
        }
    }

    @Override
    public List<CollectionEntry> findByOwnerId(int ownerId) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return findByOwnerId(ownerId, conn);
        }
    }

    @Override
    public List<CollectionEntry> findAll() throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return findAll(conn);
        }
    }

    @Override
    public boolean update(CollectionEntry entry) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return update(entry, conn);
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return delete(id, conn);
        }
    }

    // -----------------------------------------------------------
    // Connection-accepting overloads (used in transactions)
    // These must NOT close the supplied Connection
    // -----------------------------------------------------------

    @Override
    public CollectionEntry create(CollectionEntry entry, Connection conn) throws Exception {
        String sql = "INSERT INTO collections (owner_id, card_id, quantity, acquired_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entry.getOwnerId());
            ps.setInt(2, entry.getCardId());
            ps.setInt(3, entry.getQuantity());
            if (entry.getAcquiredDate() != null) ps.setDate(4, entry.getAcquiredDate());
            else ps.setDate(4, new Date(System.currentTimeMillis()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entry.setCollectionId(rs.getInt(1));
            }
            return entry;
        }
    }

    @Override
    public Optional<CollectionEntry> findById(int id, Connection conn) throws Exception {
        String sql = "SELECT collection_id, owner_id, card_id, quantity, acquired_date FROM collections WHERE collection_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                else return Optional.empty();
            }
        }
    }

    @Override
    public List<CollectionEntry> findByOwnerId(int ownerId, Connection conn) throws Exception {
        List<CollectionEntry> list = new ArrayList<>();
        String sql = "SELECT collection_id, owner_id, card_id, quantity, acquired_date FROM collections WHERE owner_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<CollectionEntry> findAll(Connection conn) throws Exception {
        List<CollectionEntry> list = new ArrayList<>();
        String sql = "SELECT collection_id, owner_id, card_id, quantity, acquired_date FROM collections";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public boolean update(CollectionEntry entry, Connection conn) throws Exception {
        String sql = "UPDATE collections SET quantity = ?, acquired_date = ? WHERE collection_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entry.getQuantity());
            ps.setDate(2, entry.getAcquiredDate());
            ps.setInt(3, entry.getCollectionId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id, Connection conn) throws Exception {
        String sql = "DELETE FROM collections WHERE collection_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // -----------------------------------------------------------
    // Helper: map result row to model
    // -----------------------------------------------------------
    private CollectionEntry mapRow(ResultSet rs) throws SQLException {
        CollectionEntry e = new CollectionEntry();
        e.setCollectionId(rs.getInt("collection_id"));
        e.setOwnerId(rs.getInt("owner_id"));
        e.setCardId(rs.getInt("card_id"));
        e.setQuantity(rs.getInt("quantity"));
        e.setAcquiredDate(rs.getDate("acquired_date"));
        return e;
    }
}