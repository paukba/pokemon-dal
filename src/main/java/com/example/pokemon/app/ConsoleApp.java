package com.example.pokemon.app;

import com.example.pokemon.dao.*;
import com.example.pokemon.model.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {
    private final OwnerDao ownerDao = new OwnerDaoImpl();
    private final CardDao cardDao = new CardDaoImpl();
    private final CollectionDao collectionDao = new CollectionDaoImpl();
    private final TradeDao tradeDao = new TradeDaoImpl();

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
        System.out.println(" 0) Exit");
    }

    private void listOwners() {
        try {
            List<Owner> owners = ownerDao.findAll();
            System.out.println("Owners:");
            for (Owner o : owners) {
                System.out.println("  " + o);
            }
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
            String line = sc.nextLine().trim();
            int id = Integer.parseInt(line);
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
            String line = sc.nextLine().trim();
            int ownerId = Integer.parseInt(line);
            List<com.example.pokemon.model.CollectionEntry> entries = collectionDao.findByOwnerId(ownerId);
            if (entries.isEmpty()) {
                System.out.println("No collection entries for owner " + ownerId);
                return;
            }
            System.out.println("Collection entries for owner " + ownerId + ":");
            for (com.example.pokemon.model.CollectionEntry e : entries) {
                System.out.println("  " + e);
            }
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
}
