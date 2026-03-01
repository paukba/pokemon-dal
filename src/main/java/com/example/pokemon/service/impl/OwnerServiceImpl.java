package com.example.pokemon.service.impl;

import com.example.pokemon.dao.CardDao;
import com.example.pokemon.dao.CollectionDao;
import com.example.pokemon.dao.OwnerDao;
import com.example.pokemon.model.Card;
import com.example.pokemon.model.CollectionEntry;
import com.example.pokemon.model.Owner;
import com.example.pokemon.service.OwnerService;
import com.example.pokemon.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Owner service implementation.
 * Computes collection value using BigDecimal arithmetic to match Card.marketValueUsd type.
 */
public class OwnerServiceImpl implements OwnerService {
    private final OwnerDao ownerDao;
    private final CollectionDao collectionDao;
    private final CardDao cardDao;

    public OwnerServiceImpl(OwnerDao ownerDao, CollectionDao collectionDao, CardDao cardDao) {
        this.ownerDao = ownerDao;
        this.collectionDao = collectionDao;
        this.cardDao = cardDao;
    }

    @Override
    public Owner createOwner(Owner owner) throws Exception {
        if (owner.getName() == null || owner.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name is required.");
        }
        return ownerDao.create(owner);
    }

    @Override
    public Optional<Owner> getOwner(int id) throws Exception {
        return ownerDao.findById(id);
    }

    @Override
    public java.util.List<Owner> listOwners() throws Exception {
        return ownerDao.findAll();
    }

    @Override
    public boolean updateOwner(Owner owner) throws Exception {
        if (owner.getOwnerId() <= 0) throw new IllegalArgumentException("Invalid owner id.");
        return ownerDao.update(owner);
    }

    /**
     * Compute total collection value (market_value_usd * qty) and return as double to preserve existing API.
     */
    @Override
    public double computeOwnerCollectionValue(int ownerId) throws Exception {
        // Use BigDecimal for accurate arithmetic, then convert to double for the interface.
        try (Connection conn = DBUtil.getConnection()) {
            BigDecimal total = BigDecimal.ZERO;
            List<CollectionEntry> entries = collectionDao.findByOwnerId(ownerId, conn);
            for (CollectionEntry e : entries) {
                int qty = e.getQuantity();
                if (qty <= 0) continue;
                Optional<Card> cardOpt = cardDao.findById(e.getCardId(), conn);
                if (cardOpt.isPresent()) {
                    Card c = cardOpt.get();
                    BigDecimal market = c.getMarketValueUsd();
                    if (market != null) {
                        // multiply and add
                        BigDecimal line = market.multiply(BigDecimal.valueOf(qty));
                        total = total.add(line);
                    }
                }
            }
            return total.doubleValue();
        }
    }
}