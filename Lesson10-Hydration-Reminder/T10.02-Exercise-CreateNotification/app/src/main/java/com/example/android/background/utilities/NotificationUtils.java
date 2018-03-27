package com.example.android.background.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;

import com.example.android.background.MainActivity;
import com.example.android.background.R;

/**
 * Utility class for creating hydration notifications
 */
public class NotificationUtils {

    private static final int REQUEST_CODE = 1;
    private static final String WATER_REMINDER_NOTIFICATION_CHANNEL_ID =
            "reminder_notification_channel";

    public static void remindUserBecauseCharging(Context context) {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(WATER_REMINDER_NOTIFICATION_CHANNEL_ID,
                            context.getString(R.string.main_notification_channel_name),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder =
                new Builder(context, WATER_REMINDER_NOTIFICATION_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.ic_drink_notification)
                        .setLargeIcon(largeIcon(context)).setContentTitle(
                        context.getString(R.string.charging_reminder_notification_title))
                        .setContentText(
                                context.getString(R.string.charging_reminder_notification_body))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                context.getString(R.string.charging_reminder_notification_body)))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentIntent(contentIntent(context)).setAutoCancel(true);

        if (VERSION.SDK_INT < VERSION_CODES.O) {

            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        }

        notificationManager.notify(REQUEST_CODE, notificationBuilder.build());

    }

    private static PendingIntent contentIntent(Context context) {

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent
                .getActivity(context, 1, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private static Bitmap largeIcon(Context context) {

        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, R.drawable.ic_local_drink_black_24px);

    }

}
