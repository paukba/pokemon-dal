package com.example.pokemon.dao;

import com.example.pokemon.model.Trade;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Trade DAO implementation with connection-accepting overloads for transactional usage.
 */
public class TradeDaoImpl implements TradeDao {

    // ---------------------------
    // No-arg methods (open connection and delegate)
    // ---------------------------

    @Override
    public Trade create(Trade trade) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return create(trade, conn);
        }
    }

    @Override
    public Optional<Trade> findById(int id) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return findById(id, conn);
        }
    }

    @Override
    public List<Trade> findAll() throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return findAll(conn);
        }
    }

    @Override
    public boolean update(Trade trade) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return update(trade, conn);
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return delete(id, conn);
        }
    }

    // ---------------------------
    // Connection-accepting overloads (used in transactions) — do NOT close conn
    // ---------------------------

    @Override
    public Trade create(Trade trade, Connection conn) throws Exception {
        String sql = "INSERT INTO trades (from_owner, to_owner, card_id, quantity, trade_date, notes, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, trade.getFromOwner());
            ps.setInt(2, trade.getToOwner());
            ps.setInt(3, trade.getCardId());
            ps.setInt(4, trade.getQuantity());

            if (trade.getTradeDate() != null) ps.setDate(5, trade.getTradeDate());
            else ps.setNull(5, Types.DATE);

            if (trade.getNotes() != null) ps.setString(6, trade.getNotes());
            else ps.setNull(6, Types.VARCHAR);

            // status default to PROPOSED if not provided
            String status = trade.getStatus() != null ? trade.getStatus() : "PROPOSED";
            ps.setString(7, status);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) trade.setTradeId(rs.getInt(1));
            }
            // set back status in case we defaulted
            trade.setStatus(status);
            return trade;
        }
    }

    @Override
    public Optional<Trade> findById(int id, Connection conn) throws Exception {
        String sql = "SELECT trade_id, from_owner, to_owner, card_id, quantity, trade_date, notes, status FROM trades WHERE trade_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Trade> findAll(Connection conn) throws Exception {
        List<Trade> list = new ArrayList<>();
        String sql = "SELECT trade_id, from_owner, to_owner, card_id, quantity, trade_date, notes, status FROM trades";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public boolean update(Trade trade, Connection conn) throws Exception {
        String sql = "UPDATE trades SET from_owner = ?, to_owner = ?, card_id = ?, quantity = ?, trade_date = ?, notes = ?, status = ? WHERE trade_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trade.getFromOwner());
            ps.setInt(2, trade.getToOwner());
            ps.setInt(3, trade.getCardId());
            ps.setInt(4, trade.getQuantity());

            if (trade.getTradeDate() != null) ps.setDate(5, trade.getTradeDate());
            else ps.setNull(5, Types.DATE);

            if (trade.getNotes() != null) ps.setString(6, trade.getNotes());
            else ps.setNull(6, Types.VARCHAR);

            if (trade.getStatus() != null) ps.setString(7, trade.getStatus());
            else ps.setNull(7, Types.VARCHAR);

            ps.setInt(8, trade.getTradeId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id, Connection conn) throws Exception {
        String sql = "DELETE FROM trades WHERE trade_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // ---------------------------
    // Helper: map ResultSet row to Trade model
    // ---------------------------
    private Trade mapRow(ResultSet rs) throws SQLException {
        Trade t = new Trade();
        t.setTradeId(rs.getInt("trade_id"));
        t.setFromOwner(rs.getInt("from_owner"));
        t.setToOwner(rs.getInt("to_owner"));
        t.setCardId(rs.getInt("card_id"));
        t.setQuantity(rs.getInt("quantity"));
        t.setTradeDate(rs.getDate("trade_date"));
        t.setNotes(rs.getString("notes"));
        t.setStatus(rs.getString("status"));
        return t;
    }
}