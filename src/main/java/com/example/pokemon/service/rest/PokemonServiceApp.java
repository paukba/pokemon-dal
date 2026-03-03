package com.example.pokemon.service.rest;

import com.example.pokemon.dao.*;
import com.example.pokemon.model.*;
import com.example.pokemon.service.*;
import com.example.pokemon.service.impl.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import static spark.Spark.*;
import spark.ExceptionHandler;

import java.util.List;
import java.util.Optional;

/**
 * PokemonServiceApp - REST wrapper around service layer.
 *
 * Hosting notes (brief):
 *  - DB configuration is read from environment variables DB_URL, DB_USER, DB_PASSWORD.
 *    If these are not set, the application falls back to src/main/resources/db.properties.
 *  - To run locally: mvn -Dexec.mainClass=com.example.pokemon.service.rest.PokemonServiceApp exec:java
 *  - To build a runnable fat JAR: mvn clean package
 *    (pom.xml is configured to produce a shaded jar; see README for hosting steps).
 *
 * CORS:
 *  - This app enables permissive CORS (Access-Control-Allow-Origin: *) for demo use.
 *    For production hosting, lock this down to your client origin.
 */
public class PokemonServiceApp {

    public static void main(String[] args) {
        final int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "4567"));
        port(port);
        System.out.println("Starting PokemonServiceApp on port " + port);
        System.out.println("Working dir: " + System.getProperty("user.dir"));

        // Serve static UI: prefer external project path for dev and fallback to classpath
        try {
            staticFiles.externalLocation(System.getProperty("user.dir") + "/src/main/resources/public");
        } catch (Exception ex) {
            System.err.println("external staticFiles location not available: " + ex.getMessage());
        }
        staticFiles.location("/public"); // classpath resource fallback

        // Enable simple permissive CORS for demo (adjust in production)
        enableCORS("*", "GET,POST,PUT,DELETE,OPTIONS", "Content-Type,Authorization");

        // health endpoint
        get("/health", (req, res) -> "OK");

        // Instantiate DAOs & Services
        OwnerDao ownerDao = new OwnerDaoImpl();
        CardDao cardDao = new CardDaoImpl();
        CollectionDao collectionDao = new CollectionDaoImpl();
        TradeDao tradeDao = new TradeDaoImpl();

        OwnerService ownerService = new OwnerServiceImpl(ownerDao, collectionDao, cardDao);
        CardService cardService = new com.example.pokemon.service.impl.CardServiceImpl(cardDao);
        CollectionService collectionService = new com.example.pokemon.service.impl.CollectionServiceImpl(collectionDao, cardDao);
        TradeService tradeService = new com.example.pokemon.service.impl.TradeServiceImpl(tradeDao, ownerDao, collectionDao);

        Gson gson = new Gson();

        // -----------------------------
        // Global exception handler (prints full stack traces to stderr and returns JSON 500)
        // -----------------------------
        exception(Exception.class, (exception, request, response) -> {
            System.err.println("Unhandled exception while processing request " + request.requestMethod() + " " + request.pathInfo());
            exception.printStackTrace(System.err);

            response.type("application/json");
            response.status(500);
            response.body("{\"success\":false,\"message\":\"Internal Server Error\"}");
        });

        // -----------------------------
        // Owners (CRUD)
        // -----------------------------
        get("/owners", (req, res) -> {
            res.type("application/json");
            return gson.toJson(ownerService.listOwners());
        });

        get("/owners/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Optional<Owner> opt = ownerService.getOwner(id);
            if (opt.isEmpty()) {
                res.status(404);
                return "";
            }
            res.type("application/json");
            return gson.toJson(opt.get());
        });

        post("/owners", (req, res) -> {
            try {
                Owner o = gson.fromJson(req.body(), Owner.class);
                Owner created = ownerService.createOwner(o);
                res.status(201);
                res.type("application/json");
                return gson.toJson(created);
            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        put("/owners/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            try {
                Owner o = gson.fromJson(req.body(), Owner.class);
                o.setOwnerId(id);
                boolean ok = ownerService.updateOwner(o);
                if (!ok) { res.status(404); return "Not found"; }
                return "";
            } catch (JsonSyntaxException | IllegalArgumentException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        delete("/owners/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            boolean ok = ownerService.deleteOwner(id);
            if (!ok) { res.status(404); return "Not found"; }
            return "";
        });

        // compute owner collection value
        get("/owners/:id/value", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            double val = ownerService.computeOwnerCollectionValue(id);
            res.type("application/json");
            return gson.toJson(val);
        });

        // -----------------------------
        // Cards (CRUD)
        // -----------------------------
        get("/cards", (req,res) -> {
            res.type("application/json");
            return gson.toJson(cardService.listCards());
        });

        get("/cards/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Optional<Card> opt = cardService.getCard(id);
            if (opt.isEmpty()) { res.status(404); return ""; }
            res.type("application/json");
            return gson.toJson(opt.get());
        });

        post("/cards", (req,res) -> {
            try {
                Card c = gson.fromJson(req.body(), Card.class);
                Card created = cardService.createCard(c);
                res.status(201);
                res.type("application/json");
                return gson.toJson(created);
            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        put("/cards/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            try {
                Card c = gson.fromJson(req.body(), Card.class);
                c.setCardId(id);
                boolean ok = cardService.updateCard(c);
                if (!ok) { res.status(404); return "Not found"; }
                return "";
            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        delete("/cards/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            boolean ok = cardService.deleteCard(id);
            if (!ok) { res.status(404); return "Not found"; }
            return "";
        });

        // -----------------------------
        // Collections (CRUD + owner-specific)
        // -----------------------------
        get("/collections", (req,res) -> {
            res.type("application/json");
            return gson.toJson(collectionService.listAll());
        });

        get("/collections/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Optional<CollectionEntry> opt = collectionService.getById(id);
            if (opt.isEmpty()) { res.status(404); return ""; }
            res.type("application/json");
            return gson.toJson(opt.get());
        });

        get("/collections/owner/:ownerId", (req,res) -> {
            int ownerId = Integer.parseInt(req.params(":ownerId"));
            res.type("application/json");
            return gson.toJson(collectionService.getCollection(ownerId));
        });

        post("/collections", (req,res) -> {
            try {
                CollectionEntry e = gson.fromJson(req.body(), CollectionEntry.class);
                CollectionEntry created = collectionService.addEntry(e);
                res.status(201);
                res.type("application/json");
                return gson.toJson(created);
            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        put("/collections/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            try {
                CollectionEntry e = gson.fromJson(req.body(), CollectionEntry.class);
                e.setCollectionId(id);
                boolean ok = collectionService.updateEntry(e);
                if (!ok) { res.status(404); return "Not found"; }
                return "";
            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        delete("/collections/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            boolean ok = collectionService.deleteEntry(id);
            if (!ok) { res.status(404); return "Not found"; }
            return "";
        });

        // -----------------------------
        // Trades (CRUD + propose/accept)
        // -----------------------------
        get("/trades", (req,res) -> {
            res.type("application/json");
            return gson.toJson(tradeDao.findAll());
        });

        get("/trades/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Optional<Trade> t = tradeService.getTrade(id);
            if (t.isEmpty()) { res.status(404); return ""; }
            res.type("application/json");
            return gson.toJson(t.get());
        });

        post("/trades/propose", (req,res) -> {
            try {
                Trade t = gson.fromJson(req.body(), Trade.class);
                Trade created = tradeService.proposeTrade(
                        t.getFromOwner(),
                        t.getToOwner(),
                        t.getCardId(),
                        t.getQuantity(),
                        t.getNotes()
                );
                res.status(201);
                res.type("application/json");
                return gson.toJson(created);
            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        post("/trades/:id/accept", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            ServiceResult result = tradeService.acceptTrade(id);
            res.type("application/json");
            return gson.toJson(result);
        });

        post("/trades", (req,res) -> {
            try {
                Trade t = gson.fromJson(req.body(), Trade.class);
                Trade created = tradeDao.create(t);
                res.status(201);
                res.type("application/json");
                return gson.toJson(created);
            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        put("/trades/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            try {
                Trade t = gson.fromJson(req.body(), Trade.class);
                t.setTradeId(id);
                boolean ok = tradeDao.update(t);
                if (!ok) { res.status(404); return "Not found"; }
                return "";
            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                res.status(400);
                return ex.getMessage();
            }
        });

        delete("/trades/:id", (req,res) -> {
            int id = Integer.parseInt(req.params(":id"));
            boolean ok = tradeDao.delete(id);
            if (!ok) { res.status(404); return "Not found"; }
            return "";
        });
        
        System.out.println("Routes configured. Server should be up. Open http://localhost:" + port + "/index.html");
    }

    /**
     * Enable CORS for the app. For demo purposes this uses permissive settings.
     * In production, restrict allowed origins/headers/methods to your needs.
     */
    private static void enableCORS(final String origin, final String methods, final String headers) {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Allow-Methods", methods);
            response.header("Access-Control-Allow-Headers", headers);
            // Note: do not set Access-Control-Allow-Credentials here for open demo
        });
    }
}