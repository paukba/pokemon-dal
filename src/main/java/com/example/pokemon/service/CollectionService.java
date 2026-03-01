package com.example.pokemon.service;

import com.example.pokemon.model.CollectionEntry;

import java.util.List;
import java.util.Optional;

public interface CollectionService {
    CollectionEntry addToCollection(int ownerId, int cardId, int qty) throws Exception;
    List<CollectionEntry> getCollection(int ownerId) throws Exception;
    Optional<CollectionEntry> getCollectionEntry(int entryId) throws Exception;
    boolean removeFromCollection(int entryId) throws Exception;
    boolean transferBetweenOwners(int fromOwnerId, int toOwnerId, int cardId, int qty) throws Exception;
}