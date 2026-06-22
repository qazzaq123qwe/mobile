package com.example.cryptopulse.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptopulse.R;
import com.example.cryptopulse.adapter.PortfolioAdapter;
import com.example.cryptopulse.model.CryptoCoin;
import com.example.cryptopulse.model.PortfolioEntry;
import com.example.cryptopulse.model.PortfolioManager;
import com.example.cryptopulse.model.SettingsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PortfolioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PortfolioAdapter adapter;
    private TextView tvTotalValue, tvTotalProfit, tvEmptyPortfolio;
    private PortfolioManager portfolioManager;
    private SettingsManager settings;
    private List<CryptoCoin> currentPrices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        portfolioManager = PortfolioManager.getInstance(this);
        settings = SettingsManager.getInstance(this);

        // Получить текущие цены от MainActivity
        currentPrices = (List<CryptoCoin>) getIntent()
                .getSerializableExtra("prices");
        if (currentPrices == null) currentPrices = new ArrayList<>();

        tvTotalValue   = findViewById(R.id.tvTotalValue);
        tvTotalProfit  = findViewById(R.id.tvTotalProfit);
        tvEmptyPortfolio = findViewById(R.id.tvEmptyPortfolio);
        recyclerView   = findViewById(R.id.recyclerPortfolio);

        adapter = new PortfolioAdapter(this, entry -> {
            portfolioManager.remove(entry.getCoinId());
            refresh();
            Toast.makeText(this, entry.getCoinName() + " удалён", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> showAddDialog());

        refresh();
    }

    private void refresh() {
        List<PortfolioEntry> entries = portfolioManager.getPortfolio();
        String symbol = settings.getCurrencySymbol();
        double rate   = settings.getCurrencyRate();

        if (entries.isEmpty()) {
            tvEmptyPortfolio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyPortfolio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(entries, currentPrices, symbol, rate);
        }

        // Общая стоимость
        double total = portfolioManager.getTotalValue(currentPrices) * rate;
        tvTotalValue.setText(String.format(Locale.US, "%s%,.2f", symbol, total));

        // Общая прибыль
        double cost = 0;
        for (PortfolioEntry e : entries) {
            cost += e.getAmount() * e.getBuyPrice() * rate;
        }
        double profit = total - cost;
        tvTotalProfit.setText(String.format(Locale.US, "Прибыль: %s%+,.2f", symbol, profit));
        tvTotalProfit.setTextColor(profit >= 0
                ? Color.parseColor("#0ECB81")
                : Color.parseColor("#F6465D"));
    }

    private void showAddDialog() {
        if (currentPrices.isEmpty()) {
            Toast.makeText(this, "Нет данных о монетах", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(android.R.layout.select_dialog_item, null);

        // Spinner для выбора монеты
        String[] coinNames = new String[currentPrices.size()];
        for (int i = 0; i < currentPrices.size(); i++) {
            coinNames[i] = currentPrices.get(i).getSymbol().toUpperCase()
                    + " — " + currentPrices.get(i).getName();
        }

        View addView = getLayoutInflater().inflate(android.R.layout.two_line_list_item, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить монету в портфолио");

        // Простой диалог с EditText-ами
        final EditText etAmount   = new EditText(this);
        final EditText etBuyPrice = new EditText(this);
        etAmount.setHint("Количество (например: 0.5)");
        etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etBuyPrice.setHint("Цена покупки в USD");
        etBuyPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, coinNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 0);
        layout.addView(spinner);
        layout.addView(etAmount);
        layout.addView(etBuyPrice);

        builder.setView(layout);
        builder.setPositiveButton("Добавить", (dialog, which) -> {
            try {
                int idx = spinner.getSelectedItemPosition();
                double amount   = Double.parseDouble(etAmount.getText().toString());
                double buyPrice = Double.parseDouble(etBuyPrice.getText().toString());
                CryptoCoin coin = currentPrices.get(idx);

                PortfolioEntry entry = new PortfolioEntry(
                        coin.getId(), coin.getName(), coin.getSymbol(),
                        coin.getImageUrl(), amount, buyPrice);
                portfolioManager.addOrUpdate(entry);
                refresh();
                Toast.makeText(this, coin.getName() + " добавлен!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}