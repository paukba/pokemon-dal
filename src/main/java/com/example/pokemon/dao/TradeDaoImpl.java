package com.example.pokemon.dao;

import com.example.pokemon.model.Trade;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradeDaoImpl implements TradeDao {
    @Override
    public Trade create(Trade trade) throws Exception {
        String sql = "INSERT INTO trades (from_owner, to_owner, card_id, quantity, trade_date, notes) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, trade.getFromOwner());
            ps.setInt(2, trade.getToOwner());
            ps.setInt(3, trade.getCardId());
            ps.setInt(4, trade.getQuantity());
            ps.setDate(5, trade.getTradeDate());
            ps.setString(6, trade.getNotes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) trade.setTradeId(rs.getInt(1));
            }
        }
        return trade;
    }

    @Override
    public Optional<Trade> findById(int id) throws Exception {
        String sql = "SELECT trade_id, from_owner, to_owner, card_id, quantity, trade_date, notes FROM trades WHERE trade_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Trade> findAll() throws Exception {
        List<Trade> list = new ArrayList<>();
        String sql = "SELECT trade_id, from_owner, to_owner, card_id, quantity, trade_date, notes FROM trades";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Trade mapRow(ResultSet rs) throws SQLException {
        Trade t = new Trade();
        t.setTradeId(rs.getInt("trade_id"));
        t.setFromOwner(rs.getInt("from_owner"));
        t.setToOwner(rs.getInt("to_owner"));
        t.setCardId(rs.getInt("card_id"));
        t.setQuantity(rs.getInt("quantity"));
        t.setTradeDate(rs.getDate("trade_date"));
        t.setNotes(rs.getString("notes"));
        return t;
    }

    @Override
    public boolean update(Trade trade) throws Exception {
        String sql = "UPDATE trades SET from_owner=?, to_owner=?, card_id=?, quantity=?, trade_date=?, notes=? WHERE trade_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, trade.getFromOwner());
            ps.setInt(2, trade.getToOwner());
            ps.setInt(3, trade.getCardId());
            ps.setInt(4, trade.getQuantity());
            ps.setDate(5, trade.getTradeDate());
            ps.setString(6, trade.getNotes());
            ps.setInt(7, trade.getTradeId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM trades WHERE trade_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }
}