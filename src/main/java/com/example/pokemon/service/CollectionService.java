package com.example.pokemon.service;

import com.example.pokemon.model.CollectionEntry;

import java.util.List;
import java.util.Optional;

public interface CollectionService {
    // constructor signature expected: CollectionServiceImpl(CollectionDao, CardDao)

    List<CollectionEntry> listAll() throws Exception;
    Optional<CollectionEntry> getById(int id) throws Exception;
    List<CollectionEntry> getCollection(int ownerId) throws Exception;

    CollectionEntry addEntry(CollectionEntry entry) throws Exception;
    boolean updateEntry(CollectionEntry entry) throws Exception;
    boolean deleteEntry(int id) throws Exception;

    /**
     * Business operation example: transfer cards between owners (not used here, but useful)
     */
    boolean transferBetweenOwners(int fromOwnerId, int toOwnerId, int cardId, int qty) throws Exception;
}