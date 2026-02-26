package com.example.pokemon.model;

import java.math.BigDecimal;

public class Card {
    private Integer cardId;
    private String name;
    private String cardSet;
    private String rarity;
    private String edition;
    private BigDecimal marketValueUsd;
    private String condition;

    public Card() {}

    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCardSet() { return cardSet; }
    public void setCardSet(String cardSet) { this.cardSet = cardSet; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public BigDecimal getMarketValueUsd() { return marketValueUsd; }
    public void setMarketValueUsd(BigDecimal marketValueUsd) { this.marketValueUsd = marketValueUsd; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    @Override
    public String toString() {
        return "Card{cardId=" + cardId +
                ", name='" + name + '\'' +
                ", cardSet='" + cardSet + '\'' +
                ", rarity='" + rarity + '\'' +
                ", edition='" + edition + '\'' +
                ", marketValueUsd=" + marketValueUsd +
                ", condition='" + condition + '\'' +
                '}';
    }
}