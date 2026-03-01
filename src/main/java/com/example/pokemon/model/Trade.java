package com.example.pokemon.model;

import java.sql.Date;

/**
 * Trade entity representing a trade between two owners.
 *
 * Added field:
 *   - status (e.g., PROPOSED, ACCEPTED)
 */
public class Trade {

    private int tradeId;
    private int fromOwner;
    private int toOwner;
    private int cardId;
    private int quantity;
    private Date tradeDate;
    private String notes;

    // ✅ NEW FIELD
    private String status;

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public int getFromOwner() {
        return fromOwner;
    }

    public void setFromOwner(int fromOwner) {
        this.fromOwner = fromOwner;
    }

    public int getToOwner() {
        return toOwner;
    }

    public void setToOwner(int toOwner) {
        this.toOwner = toOwner;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ✅ NEW GETTER
    public String getStatus() {
        return status;
    }

    // ✅ NEW SETTER
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", fromOwner=" + fromOwner +
                ", toOwner=" + toOwner +
                ", cardId=" + cardId +
                ", quantity=" + quantity +
                ", tradeDate=" + tradeDate +
                ", notes='" + notes + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}