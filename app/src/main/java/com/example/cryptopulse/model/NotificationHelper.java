package com.example.cryptopulse.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.cryptopulse.R;

public class NotificationHelper {

    private static final String CHANNEL_ID   = "crypto_alerts";
    private static final String CHANNEL_NAME = "Криптоалерты";
    private static int notifId = 1000;

    public static void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Уведомления об изменении цен криптовалют");
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    public static void sendPriceAlert(Context ctx, String coinName,
                                      double change, double price, String symbol) {
        SettingsManager settings = SettingsManager.getInstance(ctx);
        if (!settings.isNotificationsEnabled()) return;

        String direction = change > 0 ? "выросла 📈" : "упала 📉";
        String title = coinName + " " + direction;
        String body  = String.format("Изменение: %+.2f%%  |  Цена: %s%.2f",
                change, symbol, price);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(notifId++, builder.build());
    }

    // Проверить монеты из вишлиста и отправить алерты
    public static void checkAndNotify(Context ctx, java.util.List<CryptoCoin> coins) {
        SettingsManager settings = SettingsManager.getInstance(ctx);
        if (!settings.isNotificationsEnabled()) return;

        float threshold = settings.getNotifThreshold();
        WatchlistManager wm = WatchlistManager.getInstance(ctx);
        String symbol = settings.getCurrencySymbol();

        for (CryptoCoin coin : coins) {
            if (wm.isInWatchlist(coin.getId())) {
                double change = coin.getPriceChangePercentage24h();
                if (Math.abs(change) >= threshold) {
                    sendPriceAlert(ctx, coin.getName(), change,
                            coin.getCurrentPrice(), symbol);
                }
            }
        }
    }
}