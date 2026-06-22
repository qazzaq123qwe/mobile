package com.example.cryptopulse.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CryptoCoinDeserializer implements JsonDeserializer<CryptoCoin> {

    @Override
    public CryptoCoin deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        CryptoCoin coin = new CryptoCoin();

        coin.setId(getStr(obj, "id"));
        coin.setName(getStr(obj, "name"));
        coin.setSymbol(getStr(obj, "symbol"));
        coin.setImageUrl(getStr(obj, "image"));
        coin.setCurrentPrice(getDouble(obj, "current_price"));
        coin.setPriceChangePercentage24h(getDouble(obj, "price_change_percentage_24h"));
        coin.setMarketCap(getLong(obj, "market_cap"));
        coin.setTotalVolume(getLong(obj, "total_volume"));
        coin.setAth(getDouble(obj, "ath"));
        coin.setMarketCapRank(getInt(obj, "market_cap_rank"));

        return coin;
    }

    private String getStr(JsonObject obj, String key) {
        try {
            JsonElement el = obj.get(key);
            if (el == null || el.isJsonNull()) return "";
            return el.getAsString();
        } catch (Exception e) { return ""; }
    }

    private double getDouble(JsonObject obj, String key) {
        try {
            JsonElement el = obj.get(key);
            if (el == null || el.isJsonNull()) return 0.0;
            return el.getAsDouble();
        } catch (Exception e) { return 0.0; }
    }

    private long getLong(JsonObject obj, String key) {
        try {
            JsonElement el = obj.get(key);
            if (el == null || el.isJsonNull()) return 0L;
            return el.getAsLong();
        } catch (Exception e) { return 0L; }
    }

    private int getInt(JsonObject obj, String key) {
        try {
            JsonElement el = obj.get(key);
            if (el == null || el.isJsonNull()) return 0;
            return el.getAsInt();
        } catch (Exception e) { return 0; }
    }
}