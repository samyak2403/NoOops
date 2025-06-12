package com.samyak.nooops;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.samyak.nooops.utils.PreferenceUtils;

public class NoOopsApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Subscribe to notification topics
        FirebaseMessaging.getInstance().subscribeToTopic("all_users");
        FirebaseMessaging.getInstance().subscribeToTopic("promotions");

        // Apply dark mode if enabled
        if (PreferenceUtils.isDarkModeEnabled(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    public static Context getAppContext() {
        return context;
    }
} 