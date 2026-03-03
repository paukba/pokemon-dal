package com.example.pokemon.dao;

import com.example.pokemon.model.Card;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CardDaoImpl - plain JDBC implementation.
 * Provides both connection-less and connection-taking overloads so the service
 * layer can control transactions when needed.
 */
public class CardDaoImpl implements CardDao {

    // --------------------------
    // Methods that manage their own Connection
    // --------------------------
    @Override
    public Card create(Card card) throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return create(card, c);
        }
    }

    @Override
    public Optional<Card> findById(int id) throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return findById(id, c);
        }
    }

    @Override
    public List<Card> findAll() throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return findAll(c);
        }
    }

    @Override
    public boolean update(Card card) throws Exception {
        try (Connection c = DBUtil.getConnection()) {
            return update(card, c);
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
    public Card create(Card card, Connection c) throws Exception {
        String sql = "INSERT INTO cards (`name`, `card_set`, `rarity`, `edition`, `market_value_usd`, `condition`) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, card.getName());
            ps.setString(2, card.getCardSet());
            ps.setString(3, card.getRarity());
            ps.setString(4, card.getEdition());
            if (card.getMarketValueUsd() != null) ps.setBigDecimal(5, card.getMarketValueUsd());
            else ps.setNull(5, Types.DECIMAL);
            ps.setString(6, card.getCondition());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) card.setCardId(rs.getInt(1));
            }
        }
        return card;
    }

    @Override
    public Optional<Card> findById(int id, Connection c) throws Exception {
        String sql = "SELECT card_id, `name`, `card_set`, `rarity`, `edition`, `market_value_usd`, `condition` FROM cards WHERE card_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Card> findAll(Connection c) throws Exception {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT card_id, `name`, `card_set`, `rarity`, `edition`, `market_value_usd`, `condition` FROM cards";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public boolean update(Card card, Connection c) throws Exception {
        String sql = "UPDATE cards SET `name` = ?, `card_set` = ?, `rarity` = ?, `edition` = ?, `market_value_usd` = ?, `condition` = ? WHERE card_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, card.getName());
            ps.setString(2, card.getCardSet());
            ps.setString(3, card.getRarity());
            ps.setString(4, card.getEdition());
            if (card.getMarketValueUsd() != null) ps.setBigDecimal(5, card.getMarketValueUsd());
            else ps.setNull(5, Types.DECIMAL);
            ps.setString(6, card.getCondition());
            ps.setInt(7, card.getCardId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id, Connection c) throws Exception {
        String sql = "DELETE FROM cards WHERE card_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // --------------------------
    // Helper: map a ResultSet row to Card
    // --------------------------
    private Card mapRow(ResultSet rs) throws SQLException {
        Card c = new Card();
        c.setCardId(rs.getInt("card_id"));
        c.setName(rs.getString("name"));
        c.setCardSet(rs.getString("card_set"));
        c.setRarity(rs.getString("rarity"));
        c.setEdition(rs.getString("edition"));
        c.setMarketValueUsd(rs.getBigDecimal("market_value_usd"));
        c.setCondition(rs.getString("condition"));
        return c;
    }
}