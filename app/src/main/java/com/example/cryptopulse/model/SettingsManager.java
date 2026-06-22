package com.example.cryptopulse.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

    private static final String PREFS = "settings";
    private static final String KEY_CURRENCY        = "currency";
    private static final String KEY_NOTIF_ENABLED   = "notif_enabled";
    private static final String KEY_NOTIF_THRESHOLD = "notif_threshold";

    private static SettingsManager instance;
    private final SharedPreferences prefs;

    private SettingsManager(Context ctx) {
        prefs = ctx.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized SettingsManager getInstance(Context ctx) {
        if (instance == null) instance = new SettingsManager(ctx);
        return instance;
    }

    public String getCurrency()  { return prefs.getString(KEY_CURRENCY, "usd"); }
    public void setCurrency(String c) { prefs.edit().putString(KEY_CURRENCY, c).apply(); }

    public boolean isNotificationsEnabled() { return prefs.getBoolean(KEY_NOTIF_ENABLED, true); }
    public void setNotificationsEnabled(boolean v) { prefs.edit().putBoolean(KEY_NOTIF_ENABLED, v).apply(); }

    public float getNotifThreshold() { return prefs.getFloat(KEY_NOTIF_THRESHOLD, 5.0f); }
    public void setNotifThreshold(float v) { prefs.edit().putFloat(KEY_NOTIF_THRESHOLD, v).apply(); }

    public String getCurrencySymbol() {
        switch (getCurrency()) {
            case "eur": return "€";
            case "rub": return "₽";
            default:    return "$";
        }
    }

    // Всегда запрашиваем USD, конвертируем локально
    public double getCurrencyRate() {
        switch (getCurrency()) {
            case "eur": return 0.92;
            case "rub": return 90.5;
            default:    return 1.0;
        }
    }

    // API всегда запрашиваем в USD
    public String getApiCurrency() {
        return "usd";
    }
}