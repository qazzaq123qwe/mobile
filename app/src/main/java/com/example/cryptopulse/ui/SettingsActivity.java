package com.example.cryptopulse.ui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptopulse.R;
import com.example.cryptopulse.model.SettingsManager;

public class SettingsActivity extends AppCompatActivity {

    private SettingsManager settings;
    private Switch switchNotifications;
    private SeekBar seekBarThreshold;
    private TextView tvThresholdValue;
    private RadioGroup rgCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = SettingsManager.getInstance(this);

        switchNotifications = findViewById(R.id.switchNotifications);
        seekBarThreshold    = findViewById(R.id.seekBarThreshold);
        tvThresholdValue    = findViewById(R.id.tvThresholdValue);
        rgCurrency          = findViewById(R.id.rgCurrency);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadSettings();
        setupListeners();
    }

    private void loadSettings() {
        // Валюта
        switch (settings.getCurrency()) {
            case "eur": rgCurrency.check(R.id.rbEur); break;
            case "rub": rgCurrency.check(R.id.rbRub); break;
            default:    rgCurrency.check(R.id.rbUsd); break;
        }

        // Уведомления
        switchNotifications.setChecked(settings.isNotificationsEnabled());

        // Порог (1–20%)
        int progress = (int) settings.getNotifThreshold();
        seekBarThreshold.setProgress(progress - 1);
        tvThresholdValue.setText(progress + "%");
    }

    private void setupListeners() {
        rgCurrency.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbEur)       settings.setCurrency("eur");
            else if (checkedId == R.id.rbRub)  settings.setCurrency("rub");
            else                               settings.setCurrency("usd");
        });

        switchNotifications.setOnCheckedChangeListener((btn, isChecked) ->
                settings.setNotificationsEnabled(isChecked));

        seekBarThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 1;
                tvThresholdValue.setText(value + "%");
                settings.setNotifThreshold(value);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}