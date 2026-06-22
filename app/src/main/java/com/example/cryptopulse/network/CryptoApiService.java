package com.example.cryptopulse.network;

import com.example.cryptopulse.model.CryptoCoin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CryptoApiService {

    /**
     * Получить список монет с рынка CoinGecko
     * Docs: https://www.coingecko.com/api/documentation
     */
    @GET("coins/markets")
    Call<List<CryptoCoin>> getMarkets(
            @Query("vs_currency") String vsCurrency,   // "usd"
            @Query("order") String order,               // "market_cap_desc"
            @Query("per_page") int perPage,             // 100
            @Query("page") int page,                    // 1
            @Query("sparkline") boolean sparkline       // false
    );
}