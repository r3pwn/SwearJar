package com.r3pwn.swearjar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor prefsEdit;

    Switch serviceSwitch;
    TextView jarAmount;
    Button donateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = getSharedPreferences("sentSMS", Context.MODE_PRIVATE);
        prefsEdit = sharedPrefs.edit();

        serviceSwitch = (Switch)findViewById(R.id.serviceSwitch);
        jarAmount = (TextView)findViewById(R.id.jarAmount);
        donateButton = (Button)findViewById(R.id.donateButton);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_SMS},
                    0);
        } else {
            // Permission has already been granted
        }

        serviceSwitch.setChecked(sharedPrefs.getBoolean("serviceEnabled", false));
        jarAmount.setText("$" + sharedPrefs.getInt("totalBill", 0) + ".00");

        if (serviceSwitch.isChecked()) {
            SmsObserverService.beginStartingService(MainActivity.this);
        }

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    prefsEdit.putBoolean("serviceEnabled", true);
                    SmsObserverService.beginStartingService(MainActivity.this);
                } else {
                    prefsEdit.putBoolean("serviceEnabled", false);
                    Intent stopIntent = new Intent(MainActivity.this, SmsObserverService.class);
                    stopService(stopIntent);
                }
                prefsEdit.commit();
            }
        });

        if (sharedPrefs.getInt("totalBill", 0) == 0) {
            donateButton.setVisibility(View.GONE);
        } else {
            donateButton.setVisibility(View.VISIBLE);
        }

        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent donateIntent = new Intent(MainActivity.this, DonateActivity.class);
                startActivity(donateIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        jarAmount.setText("$" + sharedPrefs.getInt("totalBill", 0) + ".00");

        if (sharedPrefs.getInt("totalBill", 0) == 0) {
            donateButton.setVisibility(View.GONE);
        } else {
            donateButton.setVisibility(View.VISIBLE);
        }
    }
}
