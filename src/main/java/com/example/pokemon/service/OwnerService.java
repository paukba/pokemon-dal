package com.example.pokemon.service;

import com.example.pokemon.model.Owner;
import java.util.List;
import java.util.Optional;

public interface OwnerService {
    Owner createOwner(Owner owner) throws Exception;
    Optional<Owner> getOwner(int id) throws Exception;
    List<Owner> listOwners() throws Exception;
    boolean updateOwner(Owner owner) throws Exception;

    // helper: compute total collection value (market_value_usd * quantity)
    double computeOwnerCollectionValue(int ownerId) throws Exception;
}