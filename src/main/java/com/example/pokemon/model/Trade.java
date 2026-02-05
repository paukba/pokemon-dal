package com.example.pokemon.model;

import java.time.LocalDate;

public class Trade {
    private int tradeId;
    private int fromOwner;
    private int toOwner;
    private int cardId;
    private int quantity;
    private LocalDate tradeDate;
    private String notes;

    public Trade() {}

    public Trade(int fromOwner, int toOwner, int cardId, int quantity, LocalDate tradeDate, String notes) {
        this.fromOwner = fromOwner; this.toOwner = toOwner; this.cardId = cardId; this.quantity = quantity;
        this.tradeDate = tradeDate; this.notes = notes;
    }

    // getters/setters...
    public int getTradeId() { return tradeId; }
    public void setTradeId(int tradeId) { this.tradeId = tradeId; }
    public int getFromOwner() { return fromOwner; }
    public void setFromOwner(int fromOwner) { this.fromOwner = fromOwner; }
    public int getToOwner() { return toOwner; }
    public void setToOwner(int toOwner) { this.toOwner = toOwner; }
    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

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
                '}';
    }
}
