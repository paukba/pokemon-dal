package com.example.pokemon.dao;

import com.example.pokemon.model.Trade;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradeDaoImpl implements TradeDao {
    @Override
    public Trade create(Trade t) throws Exception {
        String sql = "INSERT INTO trades (from_owner, to_owner, card_id, quantity, trade_date, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getFromOwner());
            ps.setInt(2, t.getToOwner());
            ps.setInt(3, t.getCardId());
            ps.setInt(4, t.getQuantity());
            ps.setDate(5, Date.valueOf(t.getTradeDate()));
            ps.setString(6, t.getNotes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setTradeId(rs.getInt(1));
            }
        }
        return t;
    }

    @Override
    public Optional<Trade> findById(int tradeId) throws Exception {
        String sql = "SELECT * FROM trades WHERE trade_id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tradeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Trade> findAll() throws Exception {
        String sql = "SELECT * FROM trades";
        List<Trade> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    @Override
    public List<Trade> findByOwnerId(int ownerId) throws Exception {
        String sql = "SELECT * FROM trades WHERE from_owner = ? OR to_owner = ?";
        List<Trade> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ps.setInt(2, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    @Override
    public boolean update(Trade t) throws Exception {
        String sql = "UPDATE trades SET from_owner=?, to_owner=?, card_id=?, quantity=?, trade_date=?, notes=? WHERE trade_id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, t.getFromOwner());
            ps.setInt(2, t.getToOwner());
            ps.setInt(3, t.getCardId());
            ps.setInt(4, t.getQuantity());
            ps.setDate(5, Date.valueOf(t.getTradeDate()));
            ps.setString(6, t.getNotes());
            ps.setInt(7, t.getTradeId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int tradeId) throws Exception {
        String sql = "DELETE FROM trades WHERE trade_id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tradeId);
            return ps.executeUpdate() > 0;
        }
    }

    private Trade map(ResultSet rs) throws SQLException {
        Trade t = new Trade();
        t.setTradeId(rs.getInt("trade_id"));
        t.setFromOwner(rs.getInt("from_owner"));
        t.setToOwner(rs.getInt("to_owner"));
        t.setCardId(rs.getInt("card_id"));
        t.setQuantity(rs.getInt("quantity"));
        Date d = rs.getDate("trade_date");
        t.setTradeDate(d != null ? d.toLocalDate() : LocalDate.now());
        t.setNotes(rs.getString("notes"));
        return t;
    }
}
