package com.example.pokemon.service.impl;

import com.example.pokemon.dao.CardDao;
import com.example.pokemon.dao.CollectionDao;
import com.example.pokemon.dao.OwnerDao;
import com.example.pokemon.model.CollectionEntry;
import com.example.pokemon.service.CollectionService;
import com.example.pokemon.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Collection service implementation updated to use java.sql.Date for acquiredDate.
 */
public class CollectionServiceImpl implements CollectionService {
    private final CollectionDao collectionDao;
    private final OwnerDao ownerDao;
    private final CardDao cardDao;

    public CollectionServiceImpl(CollectionDao collectionDao, OwnerDao ownerDao, CardDao cardDao) {
        this.collectionDao = collectionDao;
        this.ownerDao = ownerDao;
        this.cardDao = cardDao;
    }

    @Override
    public CollectionEntry addToCollection(int ownerId, int cardId, int qty) throws Exception {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (!ownerDao.findById(ownerId).isPresent()) throw new IllegalArgumentException("Owner not found.");
        if (!cardDao.findById(cardId).isPresent()) throw new IllegalArgumentException("Card not found.");

        CollectionEntry e = new CollectionEntry();
        e.setOwnerId(ownerId);
        e.setCardId(cardId);
        e.setQuantity(qty);
        // Use java.sql.Date to match CollectionEntry#setAcquiredDate(java.sql.Date)
        e.setAcquiredDate(new Date(System.currentTimeMillis()));
        return collectionDao.create(e);
    }

    @Override
    public List<CollectionEntry> getCollection(int ownerId) throws Exception {
        return collectionDao.findByOwnerId(ownerId);
    }

    @Override
    public Optional<CollectionEntry> getCollectionEntry(int entryId) throws Exception {
        return collectionDao.findById(entryId);
    }

    @Override
    public boolean removeFromCollection(int entryId) throws Exception {
        return collectionDao.delete(entryId);
    }

    @Override
    public boolean transferBetweenOwners(int fromOwnerId, int toOwnerId, int cardId, int qty) throws Exception {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);

                if (!ownerDao.findById(fromOwnerId, conn).isPresent()) throw new IllegalArgumentException("From owner not found.");
                if (!ownerDao.findById(toOwnerId, conn).isPresent()) throw new IllegalArgumentException("To owner not found.");

                boolean found = false;
                List<CollectionEntry> fromEntries = collectionDao.findByOwnerId(fromOwnerId, conn);
                for (CollectionEntry entry : fromEntries) {
                    if (entry.getCardId() == cardId) {
                        if (entry.getQuantity() < qty) throw new IllegalArgumentException("From owner does not have enough quantity.");
                        entry.setQuantity(entry.getQuantity() - qty);
                        collectionDao.update(entry, conn);
                        found = true;
                        break;
                    }
                }
                if (!found) throw new IllegalArgumentException("From owner does not have that card.");

                Optional<CollectionEntry> toEntryOpt = collectionDao.findByOwnerId(toOwnerId, conn)
                        .stream().filter(e -> e.getCardId() == cardId).findFirst();

                if (toEntryOpt.isPresent()) {
                    CollectionEntry toEntry = toEntryOpt.get();
                    toEntry.setQuantity(toEntry.getQuantity() + qty);
                    collectionDao.update(toEntry, conn);
                } else {
                    CollectionEntry newEntry = new CollectionEntry();
                    newEntry.setOwnerId(toOwnerId);
                    newEntry.setCardId(cardId);
                    newEntry.setQuantity(qty);
                    newEntry.setAcquiredDate(new Date(System.currentTimeMillis()));
                    collectionDao.create(newEntry, conn);
                }

                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}