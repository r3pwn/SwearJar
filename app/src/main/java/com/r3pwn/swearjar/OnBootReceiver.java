package com.r3pwn.swearjar;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class OnBootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, SmsObserverService.class);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            context.startForegroundService(notificationIntent);
        } else {
            context.startService(notificationIntent);
        }
    }
}
