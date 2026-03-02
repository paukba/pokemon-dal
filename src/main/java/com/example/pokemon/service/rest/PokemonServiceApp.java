package com.example.pokemon.service.rest;

import com.example.pokemon.dao.*;
import com.example.pokemon.model.Trade;
import com.example.pokemon.service.*;
import com.example.pokemon.service.impl.*;
import com.google.gson.Gson;

import static spark.Spark.*;

/**
 * Defensive PokemonServiceApp - prints startup logs and exposes /health.
 * Replace your current class with this while debugging.
 */
public class PokemonServiceApp {

    public static void main(String[] args) {
        // configure port
        final int port = 4567;
        port(port);

        // Print quick startup info
        System.out.println("Starting PokemonServiceApp on port " + port);
        System.out.println("Working dir: " + System.getProperty("user.dir"));

        // Dev-friendly: serve static files from project folder (useful when running from IDE / mvn)
        // and also try classpath location. The externalLocation ensures the src/.../public folder is served.
        try {
            staticFiles.externalLocation(System.getProperty("user.dir") + "/src/main/resources/public");
            staticFiles.location("/public"); // fallback to classpath resource
            System.out.println("Configured static files: external + classpath (/public).");
        } catch (Exception ex) {
            System.err.println("Warning: static file configuration failed: " + ex.getMessage());
            ex.printStackTrace();
        }

        // immediately expose a health check so we know if routes are reachable
        get("/health", (req, res) -> {
            res.type("text/plain");
            return "OK";
        });

        // Wrap service startup in try/catch and print any exceptions
        try {
            System.out.println("Initializing DAOs and services...");

            OwnerDao ownerDao = new OwnerDaoImpl();
            CardDao cardDao = new CardDaoImpl();
            CollectionDao collectionDao = new CollectionDaoImpl();
            TradeDao tradeDao = new TradeDaoImpl();

            OwnerService ownerService = new OwnerServiceImpl(ownerDao, collectionDao, cardDao);
            TradeService tradeService = new TradeServiceImpl(tradeDao, ownerDao, collectionDao);

            Gson gson = new Gson();

            // Routes
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

            // some convenience / debug endpoints (optional)
            get("/owners", (req, res) -> {
                res.type("application/json");
                return gson.toJson(ownerService.listOwners());
            });

            get("/cards", (req, res) -> {
                res.type("application/json");
                return gson.toJson(cardDao.findAll());
            });

            get("/collections", (req, res) -> {
                res.type("application/json");
                return gson.toJson(collectionDao.findAll());
            });

            get("/trades", (req, res) -> {
                res.type("application/json");
                return gson.toJson(tradeDao.findAll());
            });

            System.out.println("Routes configured. Server should be up. Open http://localhost:" + port + "/index.html or /health");
        } catch (Exception ex) {
            System.err.println("Failed to initialize REST service: " + ex.getMessage());
            ex.printStackTrace();
            // stop Spark immediately
            stop();
        }
    }
}