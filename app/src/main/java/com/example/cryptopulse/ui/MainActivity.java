package com.example.cryptopulse.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptopulse.R;
import com.example.cryptopulse.adapter.CryptoAdapter;
import com.example.cryptopulse.model.CryptoCoin;
import com.example.cryptopulse.model.NotificationHelper;
import com.example.cryptopulse.model.SettingsManager;
import com.example.cryptopulse.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // UI
    private RecyclerView recyclerView;
    private CryptoAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvLastUpdated, tvCoinCount, tvConverterResult, tvSelectedCoin;
    private EditText etConverterAmount, etSearch;
    private ImageButton btnRefresh, btnWatchlistOpen, btnPortfolio, btnSettings;
    private TextView btnSortRank, btnSortPrice, btnSortChange;

    // Данные
    private List<CryptoCoin> allCoins = new ArrayList<>();
    private List<CryptoCoin> filteredCoins = new ArrayList<>();

    // Конвертер
    private CryptoCoin selectedConverterCoin = null;

    // Сортировка: 0=рейтинг, 1=цена, 2=изменение
    private int sortMode = 0;

    private static final int REQUEST_SETTINGS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.createChannel(this);

        initViews();
        setupRecyclerView();
        setupSearch();
        setupSort();
        setupConverter();
        setupButtons();
        fetchData();
    }

    private void initViews() {
        recyclerView      = findViewById(R.id.recyclerView);
        progressBar       = findViewById(R.id.progressBar);
        tvLastUpdated     = findViewById(R.id.tvLastUpdated);
        tvCoinCount       = findViewById(R.id.tvCoinCount);
        tvConverterResult = findViewById(R.id.tvConverterResult);
        tvSelectedCoin    = findViewById(R.id.tvSelectedCoin);
        etConverterAmount = findViewById(R.id.etConverterAmount);
        etSearch          = findViewById(R.id.etSearch);
        btnRefresh        = findViewById(R.id.btnRefresh);
        btnWatchlistOpen  = findViewById(R.id.btnWatchlistOpen);
        btnPortfolio      = findViewById(R.id.btnPortfolio);
        btnSettings       = findViewById(R.id.btnSettings);
        btnSortRank       = findViewById(R.id.btnSortRank);
        btnSortPrice      = findViewById(R.id.btnSortPrice);
        btnSortChange     = findViewById(R.id.btnSortChange);
    }

    private void setupRecyclerView() {
        adapter = new CryptoAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    // ─── ПОИСК ───────────────────────────────────────────────
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterAndSort();
            }
        });
    }

    // ─── СОРТИРОВКА ──────────────────────────────────────────
    private void setupSort() {
        btnSortRank.setOnClickListener(v  -> { sortMode = 0; filterAndSort(); updateSortButtons(); });
        btnSortPrice.setOnClickListener(v -> { sortMode = 1; filterAndSort(); updateSortButtons(); });
        btnSortChange.setOnClickListener(v-> { sortMode = 2; filterAndSort(); updateSortButtons(); });
    }

    private void updateSortButtons() {
        int inactive = Color.parseColor("#44FFFFFF");
        btnSortRank.setTextColor(inactive);
        btnSortPrice.setTextColor(inactive);
        btnSortChange.setTextColor(inactive);
        btnSortRank.setBackgroundResource(R.drawable.bg_input);
        btnSortPrice.setBackgroundResource(R.drawable.bg_input);
        btnSortChange.setBackgroundResource(R.drawable.bg_input);

        TextView active = sortMode == 0 ? btnSortRank
                : sortMode == 1 ? btnSortPrice
                : btnSortChange;
        active.setTextColor(Color.parseColor("#0ECB81"));
        active.setBackgroundResource(R.drawable.bg_badge_green);
    }

    private void filterAndSort() {
        String query = etSearch.getText().toString().trim().toLowerCase();

        filteredCoins = new ArrayList<>();
        for (CryptoCoin coin : allCoins) {
            if (query.isEmpty()
                    || coin.getName().toLowerCase().contains(query)
                    || coin.getSymbol().toLowerCase().contains(query)) {
                filteredCoins.add(coin);
            }
        }

        switch (sortMode) {
            case 1:
                filteredCoins.sort((a, b) ->
                        Double.compare(b.getCurrentPrice(), a.getCurrentPrice()));
                break;
            case 2:
                filteredCoins.sort((a, b) ->
                        Double.compare(b.getPriceChangePercentage24h(),
                                a.getPriceChangePercentage24h()));
                break;
            default:
                filteredCoins.sort(Comparator.comparingInt(CryptoCoin::getMarketCapRank));
                break;
        }

        adapter.setData(filteredCoins);
        tvCoinCount.setText(filteredCoins.size() + " монет");
    }

    // ─── КОНВЕРТЕР ───────────────────────────────────────────
    private void setupConverter() {
        etConverterAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable s) { updateConverter(); }
        });

        tvSelectedCoin.setOnClickListener(v -> showCoinPickerDialog());
    }

    private void showCoinPickerDialog() {
        if (allCoins.isEmpty()) {
            Toast.makeText(this, "Сначала загрузите данные", Toast.LENGTH_SHORT).show();
            return;
        }

        List<CryptoCoin> top20 = allCoins.subList(0, Math.min(20, allCoins.size()));
        String[] names = new String[top20.size()];
        for (int i = 0; i < top20.size(); i++) {
            names[i] = top20.get(i).getSymbol().toUpperCase()
                    + "  —  " + top20.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Выберите монету")
                .setItems(names, (dialog, which) -> {
                    selectedConverterCoin = top20.get(which);
                    tvSelectedCoin.setText(
                            selectedConverterCoin.getSymbol().toUpperCase() + " ▾");
                    updateConverter();
                })
                .show();
    }

    private void updateConverter() {
        SettingsManager settings = SettingsManager.getInstance(this);
        if (selectedConverterCoin == null) {
            tvConverterResult.setText(settings.getCurrencySymbol() + "0.00");
            return;
        }
        String amountStr = etConverterAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            tvConverterResult.setText(settings.getCurrencySymbol() + "0.00");
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            double result = amount * selectedConverterCoin.getCurrentPrice()
                    * settings.getCurrencyRate();
            tvConverterResult.setText(String.format(Locale.US,
                    "%s%,.2f", settings.getCurrencySymbol(), result));
        } catch (NumberFormatException e) {
            tvConverterResult.setText(settings.getCurrencySymbol() + "0.00");
        }
    }

    // ─── КНОПКИ ──────────────────────────────────────────────
    private void setupButtons() {
        btnRefresh.setOnClickListener(v -> {
            btnRefresh.animate()
                    .rotation(btnRefresh.getRotation() + 360f)
                    .setDuration(500)
                    .start();
            fetchData();
        });

        btnWatchlistOpen.setOnClickListener(v ->
                startActivity(new Intent(this, WatchlistActivity.class)));

        btnPortfolio.setOnClickListener(v -> {
            Intent intent = new Intent(this, PortfolioActivity.class);
            intent.putExtra("prices", new ArrayList<>(allCoins));
            startActivity(intent);
        });

        // Открываем настройки с requestCode чтобы поймать возврат
        btnSettings.setOnClickListener(v ->
                startActivityForResult(
                        new Intent(this, SettingsActivity.class), REQUEST_SETTINGS));
    }

    // ─── ВОЗВРАТ ИЗ НАСТРОЕК ─────────────────────────────────
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS) {
            // Перезагружаем данные с новой валютой
            fetchData();
            updateConverter();
        }
    }

    // ─── ЗАГРУЗКА ДАННЫХ ─────────────────────────────────────
    private void fetchData() {
        showLoading(true);

        SettingsManager settings = SettingsManager.getInstance(this);

        RetrofitClient.getInstance()
                .getApiService()
                .getMarkets("usd", "market_cap_desc", 100, 1, false) // всегда usd
                .enqueue(new Callback<List<CryptoCoin>>() {

                    @Override
                    public void onResponse(Call<List<CryptoCoin>> call,
                                           Response<List<CryptoCoin>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            allCoins = response.body();

                            // BTC по умолчанию для конвертера
                            if (selectedConverterCoin == null) {
                                for (CryptoCoin c : allCoins) {
                                    if ("bitcoin".equals(c.getId())) {
                                        selectedConverterCoin = c;
                                        tvSelectedCoin.setText("BTC ▾");
                                        break;
                                    }
                                }
                            } else {
                                // Обновить цену выбранной монеты
                                for (CryptoCoin c : allCoins) {
                                    if (c.getId().equals(selectedConverterCoin.getId())) {
                                        selectedConverterCoin = c;
                                        break;
                                    }
                                }
                            }

                            filterAndSort();
                            updateConverter();

                            // Уведомления для монет из вишлиста
                            NotificationHelper.checkAndNotify(MainActivity.this, allCoins);

                            String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(new Date());
                            tvLastUpdated.setText("Рынок (24ч)  •  " + time);

                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Ошибка сервера: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CryptoCoin>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(MainActivity.this,
                                t.getClass().getSimpleName() + ": " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}