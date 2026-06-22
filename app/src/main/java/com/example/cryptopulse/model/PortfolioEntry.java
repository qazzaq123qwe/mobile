package com.example.cryptopulse.model;

import java.io.Serializable;

public class PortfolioEntry implements Serializable {
    private String coinId;
    private String coinName;
    private String coinSymbol;
    private String imageUrl;
    private double amount;
    private double buyPrice;

    public PortfolioEntry() {}

    public PortfolioEntry(String coinId, String coinName, String coinSymbol,
                          String imageUrl, double amount, double buyPrice) {
        this.coinId     = coinId;
        this.coinName   = coinName;
        this.coinSymbol = coinSymbol;
        this.imageUrl   = imageUrl;
        this.amount     = amount;
        this.buyPrice   = buyPrice;
    }

    public String getCoinId()     { return coinId; }
    public String getCoinName()   { return coinName; }
    public String getCoinSymbol() { return coinSymbol; }
    public String getImageUrl()   { return imageUrl; }
    public double getAmount()     { return amount; }
    public double getBuyPrice()   { return buyPrice; }
    public void setAmount(double amount) { this.amount = amount; }
}