package com.example.pokemon.service.rest;

import com.example.pokemon.dao.*;
import com.example.pokemon.model.Trade;
import com.example.pokemon.service.*;
import com.example.pokemon.service.impl.*;
import com.google.gson.Gson;

import static spark.Spark.*;

/**
 * Simple REST microservice wrapper around the business/service layer.
 *
 * Runs on port 4567 by default.
 * Start with:
 *   mvn exec:java -Dexec.mainClass=com.example.pokemon.service.rest.PokemonServiceApp
 */
public class PokemonServiceApp {

    public static void main(String[] args) throws Exception {

        port(4567);

        Gson gson = new Gson();

        // DAOs (your implementations live in com.example.pokemon.dao)
        OwnerDao ownerDao = new OwnerDaoImpl();
        CardDao cardDao = new CardDaoImpl();
        CollectionDao collectionDao = new CollectionDaoImpl();
        TradeDao tradeDao = new TradeDaoImpl();

        // Services
        OwnerService ownerService = new OwnerServiceImpl(ownerDao, collectionDao, cardDao);
        TradeService tradeService = new TradeServiceImpl(tradeDao, ownerDao, collectionDao);

        // -------------------------
        // Routes
        // -------------------------

        post("/trades/propose", (req, res) -> {
            Trade t = gson.fromJson(req.body(), Trade.class);
            Trade created = tradeService.proposeTrade(
                    t.getFromOwner(),
                    t.getToOwner(),
                    t.getCardId(),
                    t.getQuantity(),
                    t.getNotes()
            );
            res.type("application/json");
            return gson.toJson(created);
        });

        post("/trades/:id/accept", (req, res) -> {
            int tradeId = Integer.parseInt(req.params(":id"));
            ServiceResult result = tradeService.acceptTrade(tradeId);
            res.type("application/json");
            return gson.toJson(result);
        });

        get("/owners/:id/value", (req, res) -> {
            int ownerId = Integer.parseInt(req.params(":id"));
            double value = ownerService.computeOwnerCollectionValue(ownerId);
            res.type("application/json");
            return gson.toJson(value);
        });
    }
}