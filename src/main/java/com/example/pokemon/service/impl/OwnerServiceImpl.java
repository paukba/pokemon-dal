package com.example.pokemon.service.impl;

import com.example.pokemon.dao.CardDao;
import com.example.pokemon.dao.CollectionDao;
import com.example.pokemon.dao.OwnerDao;
import com.example.pokemon.model.Card;
import com.example.pokemon.model.CollectionEntry;
import com.example.pokemon.model.Owner;
import com.example.pokemon.service.OwnerService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Owner service implementation. Wraps OwnerDao and uses CollectionDao/CardDao to compute totals.
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
        if (owner == null) throw new IllegalArgumentException("Owner cannot be null");
        if (owner.getName() == null || owner.getName().trim().isEmpty())
            throw new IllegalArgumentException("Owner name required");
        return ownerDao.create(owner);
    }

    @Override
    public Optional<Owner> getOwner(int id) throws Exception {
        return ownerDao.findById(id);
    }

    @Override
    public List<Owner> listOwners() throws Exception {
        return ownerDao.findAll();
    }

    @Override
    public boolean updateOwner(Owner owner) throws Exception {
        if (owner == null || owner.getOwnerId() == null) throw new IllegalArgumentException("Owner id required");
        return ownerDao.update(owner);
    }

    @Override
    public boolean deleteOwner(int id) throws Exception {
        return ownerDao.delete(id);
    }

    @Override
    public double computeOwnerCollectionValue(int ownerId) throws Exception {
        List<CollectionEntry> entries = collectionDao.findByOwnerId(ownerId);
        BigDecimal total = BigDecimal.ZERO;
        for (CollectionEntry e : entries) {
            Optional<Card> copt = cardDao.findById(e.getCardId());
            if (copt.isPresent()) {
                Card c = copt.get();
                BigDecimal mv = c.getMarketValueUsd() == null ? BigDecimal.ZERO : c.getMarketValueUsd();
                BigDecimal qty = BigDecimal.valueOf(e.getQuantity());
                total = total.add(mv.multiply(qty));
            }
        }
        return total.doubleValue();
    }
}