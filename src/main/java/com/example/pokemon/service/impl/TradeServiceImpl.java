package com.example.pokemon.service.impl;

import com.example.pokemon.dao.CollectionDao;
import com.example.pokemon.dao.OwnerDao;
import com.example.pokemon.dao.TradeDao;
import com.example.pokemon.model.CollectionEntry;
import com.example.pokemon.model.Trade;
import com.example.pokemon.model.Owner;
import com.example.pokemon.service.ServiceResult;
import com.example.pokemon.service.TradeService;
import com.example.pokemon.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Trade service:
 * - proposeTrade creates a trade record (status = PROPOSED)
 * - acceptTrade validates and executes the transfer using a single DB transaction,
 *   updating collections and trade status atomically.
 */
public class TradeServiceImpl implements TradeService {
    private final TradeDao tradeDao;
    private final OwnerDao ownerDao;
    private final CollectionDao collectionDao;

    public TradeServiceImpl(TradeDao tradeDao, OwnerDao ownerDao, CollectionDao collectionDao) {
        this.tradeDao = tradeDao;
        this.ownerDao = ownerDao;
        this.collectionDao = collectionDao;
    }

    @Override
    public Trade proposeTrade(int fromOwnerId, int toOwnerId, int cardId, int qty, String notes) throws Exception {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        if (!ownerDao.findById(fromOwnerId).isPresent()) throw new IllegalArgumentException("From owner not found.");
        if (!ownerDao.findById(toOwnerId).isPresent()) throw new IllegalArgumentException("To owner not found.");

        Trade t = new Trade();
        t.setFromOwner(fromOwnerId);
        t.setToOwner(toOwnerId);
        t.setCardId(cardId);
        t.setQuantity(qty);
        // use java.sql.Date to match model setter
        t.setTradeDate(new Date(System.currentTimeMillis()));
        t.setNotes(notes);
        t.setStatus("PROPOSED");
        return tradeDao.create(t);
    }

    @Override
    public ServiceResult acceptTrade(int tradeId) throws Exception {
        // Get trade and run a transaction to update collections and trade status atomically
        try (Connection conn = DBUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);
                Optional<Trade> opt = tradeDao.findById(tradeId, conn);
                if (!opt.isPresent()) return ServiceResult.fail("Trade not found: " + tradeId);
                Trade t = opt.get();
                if (!"PROPOSED".equalsIgnoreCase(t.getStatus())) {
                    return ServiceResult.fail("Trade is not in PROPOSED status.");
                }
                int fromOwner = t.getFromOwner();
                int toOwner = t.getToOwner();
                int cardId = t.getCardId();
                int qty = t.getQuantity();

                // Validate fromOwner has enough cards
                boolean found = false;
                for (CollectionEntry e : collectionDao.findByOwnerId(fromOwner, conn)) {
                    if (e.getCardId() == cardId) {
                        if (e.getQuantity() < qty) {
                            return ServiceResult.fail("From owner does not have enough quantity to complete trade.");
                        }
                        // decrement
                        e.setQuantity(e.getQuantity() - qty);
                        collectionDao.update(e, conn);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return ServiceResult.fail("From owner does not own this card.");
                }

                // credit the toOwner
                Optional<CollectionEntry> toEntryOpt = collectionDao.findByOwnerId(toOwner, conn)
                        .stream().filter(en -> en.getCardId() == cardId).findFirst();
                if (toEntryOpt.isPresent()) {
                    CollectionEntry toEntry = toEntryOpt.get();
                    toEntry.setQuantity(toEntry.getQuantity() + qty);
                    collectionDao.update(toEntry, conn);
                } else {
                    CollectionEntry newEntry = new CollectionEntry();
                    newEntry.setOwnerId(toOwner);
                    newEntry.setCardId(cardId);
                    newEntry.setQuantity(qty);
                    // use java.sql.Date to match model setter
                    newEntry.setAcquiredDate(new Date(System.currentTimeMillis()));
                    collectionDao.create(newEntry, conn);
                }

                // update trade status
                t.setStatus("ACCEPTED");
                tradeDao.update(t, conn);

                conn.commit();
                return ServiceResult.ok("Trade accepted and executed.");
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public Optional<Trade> getTrade(int tradeId) throws Exception {
        return tradeDao.findById(tradeId);
    }

    @Override
    public List<Trade> listTrades() throws Exception {
        return tradeDao.findAll();
    }
}