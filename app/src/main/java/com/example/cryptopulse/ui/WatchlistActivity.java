package com.example.cryptopulse.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptopulse.R;
import com.example.cryptopulse.adapter.CryptoAdapter;
import com.example.cryptopulse.model.CryptoCoin;
import com.example.cryptopulse.model.WatchlistManager;

import java.util.List;

public class WatchlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CryptoAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        recyclerView = findViewById(R.id.recyclerViewWatchlist);
        tvEmpty = findViewById(R.id.tvEmpty);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        adapter = new CryptoAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadWatchlist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWatchlist();
    }

    private void loadWatchlist() {
        List<CryptoCoin> list = WatchlistManager.getInstance(this).getWatchlist();
        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(list);
        }
    }
}