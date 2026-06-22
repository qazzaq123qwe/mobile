package com.example.cryptopulse.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WatchlistManager {

    private static final String PREFS_NAME = "cryptopulse_prefs";
    private static final String KEY_WATCHLIST = "watchlist";
    private static WatchlistManager instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private WatchlistManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized WatchlistManager getInstance(Context context) {
        if (instance == null) {
            instance = new WatchlistManager(context);
        }
        return instance;
    }

    public List<CryptoCoin> getWatchlist() {
        String json = prefs.getString(KEY_WATCHLIST, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<CryptoCoin>>() {}.getType();
        List<CryptoCoin> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public boolean isInWatchlist(String coinId) {
        for (CryptoCoin c : getWatchlist()) {
            if (c.getId().equals(coinId)) return true;
        }
        return false;
    }

    public void addToWatchlist(CryptoCoin coin) {
        List<CryptoCoin> list = getWatchlist();
        if (!isInWatchlist(coin.getId())) {
            list.add(coin);
            save(list);
        }
    }

    public void removeFromWatchlist(String coinId) {
        List<CryptoCoin> list = getWatchlist();
        list.removeIf(c -> c.getId().equals(coinId));
        save(list);
    }

    public void toggle(CryptoCoin coin) {
        if (isInWatchlist(coin.getId())) {
            removeFromWatchlist(coin.getId());
        } else {
            addToWatchlist(coin);
        }
    }

    private void save(List<CryptoCoin> list) {
        prefs.edit().putString(KEY_WATCHLIST, gson.toJson(list)).apply();
    }
}