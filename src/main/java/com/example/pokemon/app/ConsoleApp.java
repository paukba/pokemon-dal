package com.example.pokemon.app;

import com.example.pokemon.dao.*;
import com.example.pokemon.model.*;
import com.example.pokemon.service.ServiceResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console UI for the Pokémon collection manager.
 *
 * I added two menu options:
 *  8) Propose and accept a trade via REST microservice (calls /trades/propose and /trades/{id}/accept).
 *  9) Compute owner's total collection value via REST microservice (calls /owners/{id}/value).
 *
 * The REST server is expected to run on http://localhost:4567 (see PokemonServiceApp).
 * If you prefer a different base URL, change the RestClient instantiation below.
 */
public class ConsoleApp {
    private final OwnerDao ownerDao = new OwnerDaoImpl();
    private final CardDao cardDao = new CardDaoImpl();
    private final CollectionDao collectionDao = new CollectionDaoImpl();
    private final TradeDao tradeDao = new TradeDaoImpl();

    // RestClient points to local service started by PokemonServiceApp (SparkJava)
    private final RestClient restClient = new RestClient("http://localhost:4567");

    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp();
        app.run();
    }

    private void run() {
        System.out.println("Pokémon Collection Console");
        System.out.println("==========================");

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Choose an option: ");
                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1" -> listOwners();
                    case "2" -> listCards();
                    case "3" -> showCardById(sc);
                    case "4" -> showOwnerCollection(sc);
                    case "5" -> listTrades();
                    case "6" -> updateOwner(sc);
                    case "7" -> updateCardPrice(sc);
                    case "8" -> proposeAndAcceptTradeViaRest(sc);
                    case "9" -> showOwnerValueViaRest(sc);
                    case "0" -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("Unknown option. Try again.");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printMenu() {
        System.out.println("Menu:");
        System.out.println(" 1) List all owners");
        System.out.println(" 2) List all cards");
        System.out.println(" 3) Show card by ID");
        System.out.println(" 4) Show owner's collection (by owner id)");
        System.out.println(" 5) List all trades");
        System.out.println(" 6) Update owner");
        System.out.println(" 7) Update card price");
        System.out.println(" 8) Propose and accept a trade (via REST demo)");
        System.out.println(" 9) Show owner total collection value (via REST)");
        System.out.println(" 0) Exit");
    }

    private void listOwners() {
        try {
            List<Owner> owners = ownerDao.findAll();
            System.out.println("Owners:");
            for (Owner o : owners) System.out.println("  " + o);
        } catch (Exception e) {
            System.err.println("Error listing owners: " + e.getMessage());
        }
    }

    private void listCards() {
        try {
            List<Card> cards = cardDao.findAll();
            System.out.println("Cards (first 50 or fewer):");
            int i = 0;
            for (Card c : cards) {
                System.out.println("  " + c);
                if (++i >= 50) break;
            }
        } catch (Exception e) {
            System.err.println("Error listing cards: " + e.getMessage());
        }
    }

    private void showCardById(Scanner sc) {
        try {
            System.out.print("Enter card id: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Optional<Card> maybe = cardDao.findById(id);
            if (maybe.isPresent()) System.out.println("Card: " + maybe.get());
            else System.out.println("No card with id " + id);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid id.");
        } catch (Exception e) {
            System.err.println("Error fetching card: " + e.getMessage());
        }
    }

    private void showOwnerCollection(Scanner sc) {
        try {
            System.out.print("Enter owner id: ");
            int ownerId = Integer.parseInt(sc.nextLine().trim());
            List<CollectionEntry> entries = collectionDao.findByOwnerId(ownerId);
            if (entries.isEmpty()) {
                System.out.println("No collection entries for owner " + ownerId);
                return;
            }
            System.out.println("Collection entries for owner " + ownerId + ":");
            for (CollectionEntry e : entries) System.out.println("  " + e);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid id.");
        } catch (Exception e) {
            System.err.println("Error fetching collection: " + e.getMessage());
        }
    }

    private void listTrades() {
        try {
            List<Trade> trades = tradeDao.findAll();
            System.out.println("Trades:");
            for (Trade t : trades) System.out.println("  " + t);
        } catch (Exception e) {
            System.err.println("Error listing trades: " + e.getMessage());
        }
    }

    private void updateOwner(Scanner sc) {
        try {
            System.out.print("Owner id to update: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Optional<Owner> o = ownerDao.findById(id);
            if (o.isEmpty()) { System.out.println("Not found."); return; }
            Owner owner = o.get();
            System.out.print("New name (enter to keep '" + owner.getName() + "'): ");
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) owner.setName(name);
            System.out.print("New email (enter to keep '" + owner.getEmail() + "'): ");
            String email = sc.nextLine().trim();
            if (!email.isEmpty()) owner.setEmail(email);
            boolean ok = ownerDao.update(owner);
            System.out.println(ok ? "Owner updated." : "Update failed.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void updateCardPrice(Scanner sc) {
        try {
            System.out.print("Card id to update: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Optional<Card> cOpt = cardDao.findById(id);
            if (cOpt.isEmpty()) { System.out.println("Not found."); return; }
            Card card = cOpt.get();
            System.out.println("Current price: " + card.getMarketValueUsd());
            System.out.print("New price: ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) { System.out.println("No change."); return; }
            // keep using BigDecimal as original project does
            card.setMarketValueUsd(new BigDecimal(s));
            boolean ok = cardDao.update(card);
            System.out.println(ok ? "Card updated." : "Update failed.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * New menu option:
     * Propose a trade via REST, then optionally accept it immediately (demonstrates service + transactions).
     *
     * Example flow:
     *  - Prompt for fromOwnerId, toOwnerId, cardId, quantity, notes
     *  - POST /trades/propose
     *  - If created, POST /trades/{id}/accept
     */
    private void proposeAndAcceptTradeViaRest(Scanner sc) {
        try {
            System.out.println("Propose a trade (you will be asked for fields):");
            System.out.print("From owner id: ");
            int from = Integer.parseInt(sc.nextLine().trim());
            System.out.print("To owner id: ");
            int to = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Card id: ");
            int cardId = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Quantity: ");
            int qty = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Notes (optional): ");
            String notes = sc.nextLine().trim();

            Trade t = new Trade();
            t.setFromOwner(from);
            t.setToOwner(to);
            t.setCardId(cardId);
            t.setQuantity(qty);
            t.setNotes(notes);

            System.out.println("Calling REST service to propose trade...");
            Trade created = restClient.proposeTrade(t);
            System.out.println("Proposed trade id: " + created.getTradeId() + ", status: " + created.getStatus());

            System.out.print("Accept trade now? (y/N): ");
            String accept = sc.nextLine().trim();
            if (accept.equalsIgnoreCase("y")) {
                System.out.println("Calling REST service to accept trade...");
                ServiceResult result = restClient.acceptTrade(created.getTradeId());
                System.out.println("Result: " + (result.isSuccess() ? "OK" : "FAILED") + " - " + result.getMessage());
            } else {
                System.out.println("Trade left in PROPOSED state.");
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid number entered.");
        } catch (Exception e) {
            System.err.println("Error while proposing/accepting trade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * New menu option:
     * Compute owner collection value by calling REST endpoint:
     *  GET /owners/{id}/value
     */
    private void showOwnerValueViaRest(Scanner sc) {
        try {
            System.out.print("Enter owner id: ");
            int ownerId = Integer.parseInt(sc.nextLine().trim());
            System.out.println("Querying REST service for owner value...");
            double value = restClient.getOwnerValue(ownerId);
            System.out.printf("Owner %d collection total value: $%.2f%n", ownerId, value);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid id.");
        } catch (Exception e) {
            System.err.println("Error fetching owner value: " + e.getMessage());
            e.printStackTrace();
        }
    }
}