package com.example.cryptopulse.network;

import com.example.cryptopulse.model.CryptoCoin;
import com.example.cryptopulse.model.CryptoCoinDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static final String BASE_URL = "https://api.coingecko.com/api/v3/";
    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0")
                            .header("Accept", "application/json")
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // Регистрируем наш безопасный десериализатор
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CryptoCoin.class, new CryptoCoinDeserializer())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public CryptoApiService getApiService() {
        return retrofit.create(CryptoApiService.class);
    }
}