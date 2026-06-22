package com.example.cryptopulse.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.cryptopulse.R;
import com.example.cryptopulse.model.CryptoCoin;
import com.example.cryptopulse.model.SettingsManager;
import com.example.cryptopulse.ui.DetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder> {

    private final Context context;
    private List<CryptoCoin> coinList;

    public CryptoAdapter(Context context) {
        this.context = context;
        this.coinList = new ArrayList<>();
    }

    public void setData(List<CryptoCoin> coins) {
        this.coinList = coins;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.crypto_item, parent, false);
        return new CryptoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder holder, int position) {
        CryptoCoin coin = coinList.get(position);
        SettingsManager settings = SettingsManager.getInstance(context);

        double rate   = settings.getCurrencyRate();
        String symbol = settings.getCurrencySymbol();

        holder.tvName.setText(coin.getName());
        holder.tvSymbol.setText(coin.getSymbol().toUpperCase());

        // Цена с учётом валюты
        double price = coin.getCurrentPrice() * rate;
        holder.tvPrice.setText(formatPrice(price, symbol));

        // Процент изменения
        double change = coin.getPriceChangePercentage24h();
        holder.tvChange.setText(String.format(Locale.US, "%+.2f%%", change));

        if (change >= 0) {
            holder.tvChange.setTextColor(Color.parseColor("#0ECB81"));
            holder.tvChange.setBackgroundResource(R.drawable.bg_badge_green);
        } else {
            holder.tvChange.setTextColor(Color.parseColor("#F6465D"));
            holder.tvChange.setBackgroundResource(R.drawable.bg_badge_red);
        }

        Glide.with(context)
                .load(coin.getImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_coin_placeholder)
                .error(R.drawable.ic_coin_placeholder)
                .into(holder.ivIcon);

        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("coin", coin);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return coinList.size(); }

    private String formatPrice(double price, String symbol) {
        if (price >= 1000) return String.format(Locale.US, "%s%,.2f", symbol, price);
        if (price >= 1)    return String.format(Locale.US, "%s%.2f", symbol, price);
        return String.format(Locale.US, "%s%.6f", symbol, price);
    }

    static class CryptoViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView ivIcon;
        TextView tvName, tvSymbol, tvPrice, tvChange;

        CryptoViewHolder(@NonNull View itemView) {
            super(itemView);
            card     = (CardView) itemView;
            ivIcon   = itemView.findViewById(R.id.ivCoinIcon);
            tvName   = itemView.findViewById(R.id.tvCoinName);
            tvSymbol = itemView.findViewById(R.id.tvCoinSymbol);
            tvPrice  = itemView.findViewById(R.id.tvCoinPrice);
            tvChange = itemView.findViewById(R.id.tvCoinChange);
        }
    }
}