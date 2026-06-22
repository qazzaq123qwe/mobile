package com.example.cryptopulse.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PortfolioManager {

    private static final String PREFS = "portfolio_prefs";
    private static final String KEY   = "portfolio";
    private static PortfolioManager instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private PortfolioManager(Context ctx) {
        prefs = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized PortfolioManager getInstance(Context ctx) {
        if (instance == null) instance = new PortfolioManager(ctx);
        return instance;
    }

    public List<PortfolioEntry> getPortfolio() {
        String json = prefs.getString(KEY, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<PortfolioEntry>>() {}.getType();
        List<PortfolioEntry> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public void addOrUpdate(PortfolioEntry entry) {
        List<PortfolioEntry> list = getPortfolio();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCoinId().equals(entry.getCoinId())) {
                list.set(i, entry);
                save(list);
                return;
            }
        }
        list.add(entry);
        save(list);
    }

    public void remove(String coinId) {
        List<PortfolioEntry> list = getPortfolio();
        list.removeIf(e -> e.getCoinId().equals(coinId));
        save(list);
    }

    public boolean has(String coinId) {
        for (PortfolioEntry e : getPortfolio())
            if (e.getCoinId().equals(coinId)) return true;
        return false;
    }

    public double getTotalValue(List<CryptoCoin> currentPrices) {
        double total = 0;
        for (PortfolioEntry entry : getPortfolio()) {
            for (CryptoCoin coin : currentPrices) {
                if (coin.getId().equals(entry.getCoinId())) {
                    total += entry.getAmount() * coin.getCurrentPrice();
                    break;
                }
            }
        }
        return total;
    }

    private void save(List<PortfolioEntry> list) {
        prefs.edit().putString(KEY, gson.toJson(list)).apply();
    }
}