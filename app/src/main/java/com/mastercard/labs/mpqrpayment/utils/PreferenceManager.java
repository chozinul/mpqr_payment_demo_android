package com.mastercard.labs.mpqrpayment.utils;

/**
* Created by kaile on 26/7/17.
*/

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

public class PreferenceManager {
    private static PreferenceManager INSTANCE;
    private SharedPreferences preferences;

    public static String SWITCH_KEY = "SMS_SWITCH_KEY";
    public static String MOBILE_KEY = "MERCHANT_MOBILE_KEY";

    public static void init(SharedPreferences preferences) {
        INSTANCE = new PreferenceManager(preferences);
        preferences.edit().clear();
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

    public void setMobileValue(String value) {
        preferences.edit().putString(MOBILE_KEY, value).apply();
    }

    public String getMobileValue() {
        return preferences.getString(MOBILE_KEY, "");
    }

    public void setNotificationPreference(boolean value) {
        preferences.edit().putBoolean(SWITCH_KEY, value).apply();
    }

    public boolean getNotificationPreference()
    {
        return preferences.getBoolean(SWITCH_KEY, false);
    }
}
