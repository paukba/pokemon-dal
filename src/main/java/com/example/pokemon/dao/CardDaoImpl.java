package com.example.pokemon.dao;

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
        String sql = "INSERT INTO cards (name, card_set, rarity, edition, market_value_usd, `condition`) VALUES (?,?,?,?,?,?)";
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
                if (rs.next()) card.setCardId(rs.getInt(1));
            }
        }
        return card;
    }

    @Override
    public Optional<Card> findById(int id) throws Exception {
        String sql = "SELECT card_id, name, card_set, rarity, edition, market_value_usd, `condition` FROM cards WHERE card_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Card card = mapRow(rs);
                    return Optional.of(card);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Card> findAll() throws Exception {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT card_id, name, card_set, rarity, edition, market_value_usd, `condition` FROM cards ORDER BY card_id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Card mapRow(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setCardId(rs.getInt("card_id"));
        card.setName(rs.getString("name"));
        card.setCardSet(rs.getString("card_set"));
        card.setRarity(rs.getString("rarity"));
        card.setEdition(rs.getString("edition"));
        BigDecimal v = rs.getBigDecimal("market_value_usd");
        card.setMarketValueUsd(v);
        card.setCondition(rs.getString("condition"));
        return card;
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
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM cards WHERE card_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }
}