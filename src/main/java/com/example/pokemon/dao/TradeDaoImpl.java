package com.example.pokemon.dao;

import com.example.pokemon.model.Trade;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TradeDaoImpl - implements TradeDao including overloads that accept a Connection
 * so service layer can run multi-step operations within a single JDBC transaction.
 */
public class TradeDaoImpl implements TradeDao {

    // --------------------------
    // Methods that manage their own Connection
    // --------------------------
    @Override
    public Trade create(Trade trade) throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return create(trade, c);
        }
    }

    @Override
    public Optional<Trade> findById(int id) throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return findById(id, c);
        }
    }

    @Override
    public List<Trade> findAll() throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return findAll(c);
        }
    }

    @Override
    public boolean update(Trade trade) throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return update(trade, c);
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return delete(id, c);
        }
    }

    // --------------------------
    // Methods that accept a Connection (do NOT close the connection)
    // --------------------------
    @Override
    public Trade create(Trade trade, Connection c) throws Exception {
        String sql = "INSERT INTO trades (from_owner, to_owner, card_id, quantity, trade_date, notes, status) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, trade.getFromOwner());
            ps.setInt(2, trade.getToOwner());
            ps.setInt(3, trade.getCardId());
            ps.setInt(4, trade.getQuantity());
            ps.setDate(5, trade.getTradeDate());
            ps.setString(6, trade.getNotes());
            ps.setString(7, trade.getStatus() == null ? "PROPOSED" : trade.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) trade.setTradeId(rs.getInt(1));
            }
        }
        return trade;
    }

    @Override
    public Optional<Trade> findById(int id, Connection c) throws Exception {
        String sql = "SELECT trade_id, from_owner, to_owner, card_id, quantity, trade_date, notes, status FROM trades WHERE trade_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Trade> findAll(Connection c) throws Exception {
        List<Trade> list = new ArrayList<>();
        String sql = "SELECT trade_id, from_owner, to_owner, card_id, quantity, trade_date, notes, status FROM trades";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public boolean update(Trade trade, Connection c) throws Exception {
        String sql = "UPDATE trades SET from_owner=?, to_owner=?, card_id=?, quantity=?, trade_date=?, notes=?, status=? WHERE trade_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, trade.getFromOwner());
            ps.setInt(2, trade.getToOwner());
            ps.setInt(3, trade.getCardId());
            ps.setInt(4, trade.getQuantity());
            ps.setDate(5, trade.getTradeDate());
            ps.setString(6, trade.getNotes());
            ps.setString(7, trade.getStatus());
            ps.setInt(8, trade.getTradeId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id, Connection c) throws Exception {
        String sql = "DELETE FROM trades WHERE trade_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // --------------------------
    // Helper: map a ResultSet row to Trade
    // --------------------------
    private Trade mapRow(ResultSet rs) throws SQLException {
        Trade t = new Trade();
        t.setTradeId(rs.getInt("trade_id"));
        t.setFromOwner(rs.getInt("from_owner"));
        t.setToOwner(rs.getInt("to_owner"));
        t.setCardId(rs.getInt("card_id"));
        t.setQuantity(rs.getInt("quantity"));
        t.setTradeDate(rs.getDate("trade_date"));
        t.setNotes(rs.getString("notes"));

        // Defensive read of optional 'status' column: if missing, ignore
        try {
            String status = null;
            // check metadata to see if status column exists
            ResultSetMetaData md = rs.getMetaData();
            boolean hasStatus = false;
            for (int i = 1; i <= md.getColumnCount(); i++) {
                if ("status".equalsIgnoreCase(md.getColumnName(i))) {
                    hasStatus = true;
                    break;
                }
            }
            if (hasStatus) status = rs.getString("status");
            t.setStatus(status);
        } catch (SQLException ex) {
            // ignore; status optional
        }

        return t;
    }
}