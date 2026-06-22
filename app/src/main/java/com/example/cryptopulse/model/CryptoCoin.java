package com.example.cryptopulse.model;

import java.io.Serializable;

public class CryptoCoin implements Serializable {

    private String id;
    private String name;
    private String symbol;
    private double currentPrice;
    private double priceChangePercentage24h;
    private String imageUrl;
    private long marketCap;
    private long totalVolume;
    private double ath;
    private int marketCapRank;

    public CryptoCoin() {}

    // Сеттеры (нужны для десериализатора)
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public void setPriceChangePercentage24h(double v) { this.priceChangePercentage24h = v; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setMarketCap(long marketCap) { this.marketCap = marketCap; }
    public void setTotalVolume(long totalVolume) { this.totalVolume = totalVolume; }
    public void setAth(double ath) { this.ath = ath; }
    public void setMarketCapRank(int marketCapRank) { this.marketCapRank = marketCapRank; }

    // Геттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public double getCurrentPrice() { return currentPrice; }
    public double getPriceChangePercentage24h() { return priceChangePercentage24h; }
    public String getImageUrl() { return imageUrl; }
    public long getMarketCap() { return marketCap; }
    public long getTotalVolume() { return totalVolume; }
    public double getAth() { return ath; }
    public int getMarketCapRank() { return marketCapRank; }
}