package com.example.pokemon.dao;

import com.example.pokemon.model.Owner;

import java.util.List;
import java.util.Optional;

public interface OwnerDao {
    Owner create(Owner owner) throws Exception;
    Optional<Owner> findById(int ownerId) throws Exception;
    List<Owner> findAll() throws Exception;
    boolean update(Owner owner) throws Exception;
    boolean delete(int ownerId) throws Exception;
}
