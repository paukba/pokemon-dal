package com.example.pokemon.dao;

import com.example.pokemon.model.CollectionEntry;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CollectionDaoImpl implements CollectionDao {
    @Override
    public CollectionEntry create(CollectionEntry entry) throws Exception {
        String sql = "INSERT INTO collections (owner_id, card_id, quantity, acquired_date) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entry.getOwnerId());
            ps.setInt(2, entry.getCardId());
            ps.setInt(3, entry.getQuantity());
            ps.setDate(4, Date.valueOf(entry.getAcquiredDate()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entry.setCollectionId(rs.getInt(1));
            }
        }
        return entry;
    }

    @Override
    public Optional<CollectionEntry> findById(int collectionId) throws Exception {
        String sql = "SELECT * FROM collections WHERE collection_id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, collectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CollectionEntry> findAll() throws Exception {
        String sql = "SELECT * FROM collections";
        List<CollectionEntry> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    @Override
    public List<CollectionEntry> findByOwnerId(int ownerId) throws Exception {
        String sql = "SELECT * FROM collections WHERE owner_id = ?";
        List<CollectionEntry> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    @Override
    public boolean update(CollectionEntry entry) throws Exception {
        String sql = "UPDATE collections SET owner_id=?, card_id=?, quantity=?, acquired_date=? WHERE collection_id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, entry.getOwnerId());
            ps.setInt(2, entry.getCardId());
            ps.setInt(3, entry.getQuantity());
            ps.setDate(4, Date.valueOf(entry.getAcquiredDate()));
            ps.setInt(5, entry.getCollectionId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int collectionId) throws Exception {
        String sql = "DELETE FROM collections WHERE collection_id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, collectionId);
            return ps.executeUpdate() > 0;
        }
    }

    private CollectionEntry map(ResultSet rs) throws SQLException {
        CollectionEntry e = new CollectionEntry();
        e.setCollectionId(rs.getInt("collection_id"));
        e.setOwnerId(rs.getInt("owner_id"));
        e.setCardId(rs.getInt("card_id"));
        e.setQuantity(rs.getInt("quantity"));
        Date d = rs.getDate("acquired_date");
        e.setAcquiredDate(d != null ? d.toLocalDate() : LocalDate.now());
        return e;
    }
}
