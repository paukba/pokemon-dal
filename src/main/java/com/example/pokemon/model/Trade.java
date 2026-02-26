package com.example.pokemon.model;

import java.sql.Date;

public class Trade {
    private Integer tradeId;
    private Integer fromOwner;
    private Integer toOwner;
    private Integer cardId;
    private Integer quantity;
    private Date tradeDate;
    private String notes;

    public Trade() {}

    public Integer getTradeId() { return tradeId; }
    public void setTradeId(Integer tradeId) { this.tradeId = tradeId; }

    public Integer getFromOwner() { return fromOwner; }
    public void setFromOwner(Integer fromOwner) { this.fromOwner = fromOwner; }

    public Integer getToOwner() { return toOwner; }
    public void setToOwner(Integer toOwner) { this.toOwner = toOwner; }

    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Trade{tradeId=" + tradeId +
                ", fromOwner=" + fromOwner +
                ", toOwner=" + toOwner +
                ", cardId=" + cardId +
                ", quantity=" + quantity +
                ", tradeDate=" + tradeDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}