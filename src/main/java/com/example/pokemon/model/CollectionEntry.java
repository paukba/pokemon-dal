package com.example.pokemon.model;

import java.sql.Date;

public class CollectionEntry {
    private Integer collectionId;
    private Integer ownerId;
    private Integer cardId;
    private Integer quantity;
    private Date acquiredDate;

    public CollectionEntry() {}

    public Integer getCollectionId() { return collectionId; }
    public void setCollectionId(Integer collectionId) { this.collectionId = collectionId; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getAcquiredDate() { return acquiredDate; }
    public void setAcquiredDate(Date acquiredDate) { this.acquiredDate = acquiredDate; }

    @Override
    public String toString() {
        return "CollectionEntry{collectionId=" + collectionId +
                ", ownerId=" + ownerId +
                ", cardId=" + cardId +
                ", quantity=" + quantity +
                ", acquiredDate=" + acquiredDate +
                '}';
    }
}