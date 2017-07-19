package com.mastercard.labs.mpqrpayment.utils;

/**
 * Created by kaile on 19/7/17.
 */

import android.content.SharedPreferences;

import java.util.Set;

public class PreferenceManager {
    private static PreferenceManager INSTANCE;

    private SharedPreferences preferences;

    public static void init(SharedPreferences preferences) {
        INSTANCE = new PreferenceManager(preferences);
    }

    public static PreferenceManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Call `PreferenceManager.init(SharedPreferences)` before calling this method.");
        }
        return INSTANCE;
    }

    private PreferenceManager(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void putStringSet(String key, Set<String> value) {
        preferences.edit().putStringSet(key, value).apply();
    }

    public Set<String> getStringSet(String key, Set<String> defaultValues) {
        return preferences.getStringSet(key, defaultValues);
    }
}
