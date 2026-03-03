package com.example.pokemon.service;

import com.example.pokemon.model.Owner;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Owner-related business logic.
 */
public interface OwnerService {
    Owner createOwner(Owner owner) throws Exception;
    Optional<Owner> getOwner(int id) throws Exception;
    List<Owner> listOwners() throws Exception;
    boolean updateOwner(Owner owner) throws Exception;
    boolean deleteOwner(int id) throws Exception;

    /**
     * Compute total collection value for an owner (sum of card.marketValueUsd * quantity).
     * Returns numeric double (for JSON serialization convenience). Uses BigDecimal internally.
     */
    double computeOwnerCollectionValue(int ownerId) throws Exception;
}