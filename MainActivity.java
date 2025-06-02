package com.example.myapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.*;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Switch notificationToggle;
    private static final String TOGGLE_PREF = "togglePref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationToggle = findViewById(R.id.notificationToggle);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isOn = prefs.getBoolean(TOGGLE_PREF, false);
        notificationToggle.setChecked(isOn);

        if (isOn) {
            startReminderWorker();
        }

        notificationToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(TOGGLE_PREF, isChecked);
            editor.apply();

            if (isChecked) {
                startReminderWorker();
            } else {
                WorkManager.getInstance(this).cancelUniqueWork("ReminderWork");
            }
        });
    }

    private void startReminderWorker() {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                ReminderWorker.class,
                20, TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "ReminderWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
        );
    }
}
