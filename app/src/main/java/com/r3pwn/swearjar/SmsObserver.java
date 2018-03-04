package com.r3pwn.swearjar;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

public class SmsObserver extends ContentObserver {

    private Context mContext;

    private String smsBodyStr = "", phoneNoStr = "";
    private long smsDatTime = System.currentTimeMillis();
    static final Uri SMS_STATUS_URI = Uri.parse("content://sms/sent");
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefsEdit;


    public SmsObserver(Handler handler, Context ctx) {
        super(handler);
        mContext = ctx;
        sharedPrefs = ctx.getSharedPreferences("sentSMS", Context.MODE_PRIVATE);
        prefsEdit = sharedPrefs.edit();
    }

    public boolean deliverSelfNotifications() {
        return true;
    }

    public void onChange(boolean selfChange) {
        try{
            Log.d("Info","Notification on SMS observer");
            Cursor sms_sent_cursor = mContext.getContentResolver().query(SMS_STATUS_URI, null, null, null, null);
            if (sms_sent_cursor != null) {
                if (sms_sent_cursor.moveToFirst()) {
                    String protocol = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("protocol"));
                    // for sent messages, protocol is null
                    if(protocol == null){
                        int type = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("type"));
                        // for actual state type=2
                        String messageUid = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("thread_id")) + "," +
                                sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("_id"));
                        String content = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body")).trim();
                        if(type == 2 && !alreadyChecked(messageUid)){
                            FilterableMessage fm = new FilterableMessage(messageUid, content);
                            Log.i("Info","New message filtered");
                            // new (previously unfiltered) message!
                            if (fm.getDollarAmount() > 0) {
                                prefsEdit.putInt("totalBill",
                                        sharedPrefs.getInt("totalBill", 0) + fm.getDollarAmount());
                                Toast.makeText(mContext, "Careful there! You got $" +
                                        fm.getDollarAmount() + ".00 added to your swear jar!",
                                        Toast.LENGTH_LONG).show();
                            }
                            addToBlacklist(fm.getUid());
                        }
                    }
                }
            }
            else
                Log.e("Info","Send Cursor is Empty");
        }
        catch(Exception e){
            Log.e("Error", "Error on onChange : "+e.toString());
        }
        super.onChange(selfChange);
    }

    private boolean alreadyChecked(String msgUid) {
        String blacklist = sharedPrefs.getString("sms_blacklist", "");
        String[] uids = blacklist.split(";");
        if (Arrays.asList(uids).contains(msgUid)) {
            return true;
        } else {
            return false;
        }
    }

    private void addToBlacklist(String msgUid) {
        String blacklist = sharedPrefs.getString("sms_blacklist", "");
        prefsEdit.putString("sms_blacklist", blacklist + msgUid + ";");
        prefsEdit.commit();
    }
}