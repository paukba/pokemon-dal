package com.example.pokemon.dao;

import com.example.pokemon.model.Card;
import com.example.pokemon.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardDaoImpl implements CardDao {

    @Override
    public Card create(Card card) throws Exception {
        String sql = "INSERT INTO cards (name, card_set, rarity, edition, market_value_usd, `condition`) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, card.getName());
            ps.setString(2, card.getCardSet());
            ps.setString(3, card.getRarity());
            ps.setString(4, card.getEdition());
            ps.setBigDecimal(5, card.getMarketValueUsd());
            ps.setString(6, card.getCondition());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    card.setCardId(rs.getInt(1));
                }
            }
        }
        return card;
    }

    @Override
    public Optional<Card> findById(int cardId) throws Exception {
        String sql = "SELECT * FROM cards WHERE card_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Card card = mapRowToCard(rs);
                    return Optional.of(card);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Card> findAll() throws Exception {
        String sql = "SELECT * FROM cards ORDER BY card_id";
        List<Card> output = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                output.add(mapRowToCard(rs));
            }
        }
        return output;
    }

    @Override
    public boolean update(Card card) throws Exception {
        String sql = "UPDATE cards SET name=?, card_set=?, rarity=?, edition=?, market_value_usd=?, `condition`=? WHERE card_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, card.getName());
            ps.setString(2, card.getCardSet());
            ps.setString(3, card.getRarity());
            ps.setString(4, card.getEdition());
            ps.setBigDecimal(5, card.getMarketValueUsd());
            ps.setString(6, card.getCondition());
            ps.setInt(7, card.getCardId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int cardId) throws Exception {
        String sql = "DELETE FROM cards WHERE card_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cardId);
            return ps.executeUpdate() > 0;
        }
    }

    private Card mapRowToCard(ResultSet rs) throws SQLException {
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
