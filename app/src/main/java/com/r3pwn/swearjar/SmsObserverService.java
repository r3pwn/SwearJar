package com.r3pwn.swearjar;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class SmsObserverService extends Service {
    private static Uri uriSMS = Uri.parse("content://sms");

    private ContentResolver crSMS;
    private SmsObserver observerSMS = null;
    private SharedPreferences sharedPrefs;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        if (BuildConfig.DEBUG) Log.v("SJ", "SmsMonitorService created");
        sharedPrefs = getSharedPreferences("sentSMS", Context.MODE_PRIVATE);
        registerSMSObserver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (sharedPrefs.getBoolean("serviceEnabled", false)) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Swear Jar")
                    .setTicker("Ticker")
                    .setContentText("Your sent messages are being monitored for profanity...")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setOngoing(true).build();

            startForeground(101, notification);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterSMSObserver();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * Registers the observer for SMS changes
     */
    private void registerSMSObserver() {
        if (sharedPrefs.getBoolean("serviceEnabled", false)) {
            if (observerSMS == null) {
                observerSMS = new SmsObserver(new Handler(), this);
                crSMS = getContentResolver();
                crSMS.registerContentObserver(uriSMS, true, observerSMS);
                if (BuildConfig.DEBUG) Log.v("SJ", "SMS Observer registered.");
            }
        }
    }

    /**
     * Unregisters the observer for call log changes
     */
    private void unregisterSMSObserver() {
        if (crSMS != null) {
            crSMS.unregisterContentObserver(observerSMS);
        }
        if (observerSMS != null) {
            observerSMS = null;
        }
        if (BuildConfig.DEBUG) Log.v("SJ", "Unregistered SMS Observer");
    }

    /**
     * Start the service to process that will run the content observer
     */
    public static void beginStartingService(Context context) {
        if (BuildConfig.DEBUG) Log.v("SJ", "SmsMonitorService: beginStartingService()");
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, SmsObserverService.class));
        } else {
            context.startService(new Intent(context, SmsObserverService.class));
        }
    }

    /**
     * Called back by the service when it has finished processing notifications,
     * releasing the wake lock if the service is now stopping.
     */
    public static void finishStartingService(Service service) {
        if (BuildConfig.DEBUG) Log.v("SJ", "SmsMonitorService: finishStartingService()");
        service.stopSelf();
    }

}