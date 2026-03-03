package com.example.pokemon.service.impl;

import com.example.pokemon.dao.CardDao;
import com.example.pokemon.dao.CollectionDao;
import com.example.pokemon.model.Card;
import com.example.pokemon.model.CollectionEntry;
import com.example.pokemon.service.CollectionService;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Collection service. Simple validation & DAO delegation.
 * Constructor matches PokemonServiceApp usage: (CollectionDao, CardDao)
 */
public class CollectionServiceImpl implements CollectionService {
    private final CollectionDao collectionDao;
    private final CardDao cardDao;

    public CollectionServiceImpl(CollectionDao collectionDao, CardDao cardDao) {
        this.collectionDao = collectionDao;
        this.cardDao = cardDao;
    }

    @Override
    public List<CollectionEntry> listAll() throws Exception {
        return collectionDao.findAll();
    }

    @Override
    public Optional<CollectionEntry> getById(int id) throws Exception {
        return collectionDao.findById(id);
    }

    @Override
    public List<CollectionEntry> getCollection(int ownerId) throws Exception {
        return collectionDao.findByOwnerId(ownerId);
    }

    @Override
    public CollectionEntry addEntry(CollectionEntry entry) throws Exception {
        if (entry == null) throw new IllegalArgumentException("Entry cannot be null");
        if (entry.getQuantity() <= 0) throw new IllegalArgumentException("Quantity must be positive");
        // ensure card exists
        Optional<Card> cOpt = cardDao.findById(entry.getCardId());
        if (cOpt.isEmpty()) throw new IllegalArgumentException("Card not found: " + entry.getCardId());
        // set acquired date if missing
        if (entry.getAcquiredDate() == null) {
            entry.setAcquiredDate(new Date(System.currentTimeMillis()));
        }
        return collectionDao.create(entry);
    }

    @Override
    public boolean updateEntry(CollectionEntry entry) throws Exception {
        if (entry == null || entry.getCollectionId() == null) throw new IllegalArgumentException("Collection id required");
        if (entry.getQuantity() < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        return collectionDao.update(entry);
    }

    @Override
    public boolean deleteEntry(int id) throws Exception {
        return collectionDao.delete(id);
    }

    @Override
    public boolean transferBetweenOwners(int fromOwnerId, int toOwnerId, int cardId, int qty) throws Exception {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive");
        // naive single-threaded implementation (no explicit transaction here).
        // For real atomic transfers, use DAO methods that accept a Connection and manage transactions as earlier.
        List<CollectionEntry> fromEntries = collectionDao.findByOwnerId(fromOwnerId);
        CollectionEntry fromEntry = fromEntries.stream().filter(e -> e.getCardId() == cardId).findFirst().orElse(null);
        if (fromEntry == null || fromEntry.getQuantity() < qty) return false;
        fromEntry.setQuantity(fromEntry.getQuantity() - qty);
        collectionDao.update(fromEntry);

        List<CollectionEntry> toEntries = collectionDao.findByOwnerId(toOwnerId);
        Optional<CollectionEntry> toOpt = toEntries.stream().filter(e -> e.getCardId() == cardId).findFirst();
        if (toOpt.isPresent()) {
            CollectionEntry toEntry = toOpt.get();
            toEntry.setQuantity(toEntry.getQuantity() + qty);
            collectionDao.update(toEntry);
        } else {
            CollectionEntry newEntry = new CollectionEntry();
            newEntry.setOwnerId(toOwnerId);
            newEntry.setCardId(cardId);
            newEntry.setQuantity(qty);
            newEntry.setAcquiredDate(new Date(System.currentTimeMillis()));
            collectionDao.create(newEntry);
        }
        return true;
    }
}