package com.example.pokemon.dao;

import com.example.pokemon.dao.CardDao;
import com.example.pokemon.model.Card;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class CardDaoImpl implements CardDao {
    @Override
    public Card create(Card card) throws Exception {
        String sql = "INSERT INTO cards (name, card_set, rarity, edition, market_value_usd, condition) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            return card;
        }
    }

    @Override
    public Optional<Card> findById(int id) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return findById(id, conn);
        }
    }

    @Override
    public List<Card> findAll() throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return findAll(conn);
        }
    }

    @Override
    public boolean update(Card card) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            return update(card, conn);
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM cards WHERE card_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // --- Connection-based overloads ---

    @Override
    public Optional<Card> findById(int id, Connection conn) throws Exception {
        String sql = "SELECT card_id, name, card_set, rarity, edition, market_value_usd, condition FROM cards WHERE card_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractCard(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public List<Card> findAll(Connection conn) throws Exception {
        String sql = "SELECT card_id, name, card_set, rarity, edition, market_value_usd, condition FROM cards";
        List<Card> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractCard(rs));
            }
        }
        return list;
    }

    @Override
    public boolean update(Card card, Connection conn) throws Exception {
        String sql = "UPDATE cards SET name=?, card_set=?, rarity=?, edition=?, market_value_usd=?, condition=? WHERE card_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, card.getName());
            ps.setString(2, card.getCardSet());
            ps.setString(3, card.getRarity());
            ps.setString(4, card.getEdition());
            if (card.getMarketValueUsd() != null) ps.setBigDecimal(5, card.getMarketValueUsd());
            else ps.setNull(5, Types.DECIMAL);
            ps.setString(6, card.getCondition());
            ps.setInt(7, card.getCardId());
            return ps.executeUpdate() > 0;
        }
    }

    private Card extractCard(ResultSet rs) throws SQLException {
        Card c = new Card();
        c.setCardId(rs.getInt("card_id"));
        c.setName(rs.getString("name"));
        c.setCardSet(rs.getString("card_set"));
        c.setRarity(rs.getString("rarity"));
        c.setEdition(rs.getString("edition"));

        BigDecimal v = rs.getBigDecimal("market_value_usd");
        c.setMarketValueUsd(v); // null-safe: setBigDecimal accepts null (we allow null market values)

        c.setCondition(rs.getString("condition"));
        return c;
    }
}