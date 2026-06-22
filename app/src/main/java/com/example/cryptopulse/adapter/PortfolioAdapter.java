package com.example.cryptopulse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptopulse.R;
import com.example.cryptopulse.model.CryptoCoin;
import com.example.cryptopulse.model.PortfolioEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    public interface OnDeleteListener { void onDelete(PortfolioEntry entry); }

    private final Context context;
    private List<PortfolioEntry> entries = new ArrayList<>();
    private List<CryptoCoin> currentPrices = new ArrayList<>();
    private final OnDeleteListener deleteListener;
    private String currencySymbol = "$";
    private double currencyRate = 1.0;

    public PortfolioAdapter(Context context, OnDeleteListener listener) {
        this.context = context;
        this.deleteListener = listener;
    }

    public void setData(List<PortfolioEntry> entries, List<CryptoCoin> prices,
                        String symbol, double rate) {
        this.entries = entries;
        this.currentPrices = prices;
        this.currencySymbol = symbol;
        this.currencyRate = rate;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.portfolio_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        PortfolioEntry entry = entries.get(pos);

        h.tvName.setText(entry.getCoinName() + " (" + entry.getCoinSymbol().toUpperCase() + ")");
        h.tvAmount.setText("Кол-во: " + entry.getAmount());

        // Найти текущую цену
        double currentPrice = 0;
        for (CryptoCoin coin : currentPrices) {
            if (coin.getId().equals(entry.getCoinId())) {
                currentPrice = coin.getCurrentPrice();
                break;
            }
        }

        double value  = entry.getAmount() * currentPrice * currencyRate;
        double cost   = entry.getAmount() * entry.getBuyPrice() * currencyRate;
        double profit = value - cost;

        h.tvValue.setText(String.format(Locale.US, "%s%,.2f", currencySymbol, value));
        h.tvProfit.setText(String.format(Locale.US, "%s%+,.2f", currencySymbol, profit));
        h.tvProfit.setTextColor(profit >= 0
                ? Color.parseColor("#0ECB81")
                : Color.parseColor("#F6465D"));

        Glide.with(context).load(entry.getImageUrl())
                .placeholder(R.drawable.ic_coin_placeholder).into(h.ivIcon);

        h.btnDelete.setOnClickListener(v -> deleteListener.onDelete(entry));
    }

    @Override public int getItemCount() { return entries.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvAmount, tvValue, tvProfit;
        ImageButton btnDelete;

        ViewHolder(View v) {
            super(v);
            ivIcon    = v.findViewById(R.id.ivIcon);
            tvName    = v.findViewById(R.id.tvCoinName);
            tvAmount  = v.findViewById(R.id.tvAmount);
            tvValue   = v.findViewById(R.id.tvValue);
            tvProfit  = v.findViewById(R.id.tvProfit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}