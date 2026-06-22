package com.example.cryptopulse.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cryptopulse.R;
import com.example.cryptopulse.model.CryptoCoin;
import com.example.cryptopulse.model.SettingsManager;
import com.example.cryptopulse.model.WatchlistManager;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageButton btnWatchlist;
    private CryptoCoin coin;
    private WatchlistManager watchlistManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        coin = (CryptoCoin) getIntent().getSerializableExtra("coin");
        if (coin == null) { finish(); return; }

        watchlistManager = WatchlistManager.getInstance(this);

        bindData(coin);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnWatchlist = findViewById(R.id.btnWatchlist);
        updateWatchlistIcon();

        btnWatchlist.setOnClickListener(v -> {
            watchlistManager.toggle(coin);
            updateWatchlistIcon();
            boolean added = watchlistManager.isInWatchlist(coin.getId());
            Toast.makeText(this,
                    added ? coin.getName() + " добавлен в вишлист"
                            : coin.getName() + " удалён из вишлиста",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void updateWatchlistIcon() {
        boolean inList = watchlistManager.isInWatchlist(coin.getId());
        btnWatchlist.setImageResource(
                inList ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );
    }

    private void bindData(CryptoCoin coin) {
        SettingsManager settings = SettingsManager.getInstance(this);
        double rate   = settings.getCurrencyRate();
        String symbol = settings.getCurrencySymbol();

        ImageView ivIcon     = findViewById(R.id.ivDetailIcon);
        TextView tvName      = findViewById(R.id.tvDetailName);
        TextView tvSymbol    = findViewById(R.id.tvDetailSymbol);
        TextView tvPrice     = findViewById(R.id.tvDetailPrice);
        TextView tvChange    = findViewById(R.id.tvDetailChange);
        TextView tvMarketCap = findViewById(R.id.tvMarketCap);
        TextView tvVolume    = findViewById(R.id.tvVolume);
        TextView tvAth       = findViewById(R.id.tvAth);
        TextView tvRank      = findViewById(R.id.tvRank);

        tvName.setText(coin.getName() + " (" + coin.getSymbol().toUpperCase() + ")");
        tvSymbol.setText(coin.getSymbol().toUpperCase() + "/USD");
        tvPrice.setText(formatPrice(coin.getCurrentPrice() * rate, symbol));
        tvRank.setText("#" + coin.getMarketCapRank());
        tvMarketCap.setText(formatLarge((long)(coin.getMarketCap() * rate), symbol));
        tvVolume.setText(formatLarge((long)(coin.getTotalVolume() * rate), symbol));
        tvAth.setText(formatPrice(coin.getAth() * rate, symbol));

        double change = coin.getPriceChangePercentage24h();
        tvChange.setText(String.format(Locale.US, "%+.2f%% за 24 часа", change));
        tvChange.setTextColor(change >= 0
                ? Color.parseColor("#0ECB81")
                : Color.parseColor("#F6465D"));

        Glide.with(this)
                .load(coin.getImageUrl())
                .placeholder(R.drawable.ic_coin_placeholder)
                .into(ivIcon);
    }

    private String formatPrice(double price, String symbol) {
        if (price >= 1000) return String.format(Locale.US, "%s%,.2f", symbol, price);
        if (price >= 1)    return String.format(Locale.US, "%s%.4f", symbol, price);
        return String.format(Locale.US, "%s%.6f", symbol, price);
    }

    private String formatLarge(long n, String symbol) {
        if (n >= 1_000_000_000_000L)
            return String.format(Locale.US, "%s%.2fT", symbol, n / 1_000_000_000_000.0);
        if (n >= 1_000_000_000L)
            return String.format(Locale.US, "%s%.2fB", symbol, n / 1_000_000_000.0);
        if (n >= 1_000_000L)
            return String.format(Locale.US, "%s%.2fM", symbol, n / 1_000_000.0);
        return String.format(Locale.US, "%s%,d", symbol, n);
    }
}