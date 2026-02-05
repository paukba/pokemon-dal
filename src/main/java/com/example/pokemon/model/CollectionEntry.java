package com.example.pokemon.model;

import java.time.LocalDate;

public class CollectionEntry {
    private int collectionId;
    private int ownerId;
    private int cardId;
    private int quantity;
    private LocalDate acquiredDate;

    public CollectionEntry() {}

    public CollectionEntry(int ownerId, int cardId, int quantity, LocalDate acquiredDate) {
        this.ownerId = ownerId; this.cardId = cardId; this.quantity = quantity; this.acquiredDate = acquiredDate;
    }

    // getters/setters...
    public int getCollectionId() { return collectionId; }
    public void setCollectionId(int collectionId) { this.collectionId = collectionId; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public java.time.LocalDate getAcquiredDate() { return acquiredDate; }
    public void setAcquiredDate(java.time.LocalDate acquiredDate) { this.acquiredDate = acquiredDate; }

    @Override
    public String toString() {
        return "CollectionEntry{" +
                "collectionId=" + collectionId +
                ", ownerId=" + ownerId +
                ", cardId=" + cardId +
                ", quantity=" + quantity +
                ", acquiredDate=" + acquiredDate +
                '}';
    }
}
