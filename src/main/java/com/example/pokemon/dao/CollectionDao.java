package com.example.pokemon.dao;

import com.example.pokemon.model.CollectionEntry;

import java.util.List;
import java.util.Optional;

public interface CollectionDao {
    CollectionEntry create(CollectionEntry entry) throws Exception;
    Optional<CollectionEntry> findById(int collectionId) throws Exception;
    List<CollectionEntry> findAll() throws Exception;
    List<CollectionEntry> findByOwnerId(int ownerId) throws Exception;
    boolean update(CollectionEntry entry) throws Exception;
    boolean delete(int collectionId) throws Exception;
}
