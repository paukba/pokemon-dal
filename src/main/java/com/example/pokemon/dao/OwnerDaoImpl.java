package com.example.pokemon.dao;

import com.example.pokemon.model.Owner;
import com.example.pokemon.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OwnerDaoImpl implements OwnerDao {

    @Override
    public Owner create(Owner owner) throws Exception {
        String sql = "INSERT INTO owners (name, email) VALUES (?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, owner.getName());
            ps.setString(2, owner.getEmail());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Creating owner failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    owner.setOwnerId(rs.getInt(1));
                }
            }
        }
        return owner;
    }

    @Override
    public Optional<Owner> findById(int ownerId) throws Exception {
        String sql = "SELECT owner_id, name, email FROM owners WHERE owner_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Owner o = new Owner(rs.getInt("owner_id"), rs.getString("name"), rs.getString("email"));
                    return Optional.of(o);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Owner> findAll() throws Exception {
        String sql = "SELECT owner_id, name, email FROM owners";
        List<Owner> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Owner(rs.getInt("owner_id"), rs.getString("name"), rs.getString("email")));
            }
        }
        return list;
    }

    @Override
    public boolean update(Owner owner) throws Exception {
        String sql = "UPDATE owners SET name = ?, email = ? WHERE owner_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, owner.getName());
            ps.setString(2, owner.getEmail());
            ps.setInt(3, owner.getOwnerId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int ownerId) throws Exception {
        String sql = "DELETE FROM owners WHERE owner_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            return ps.executeUpdate() > 0;
        }
    }
}
