package com.mastercard.labs.mpqrpayment.network;

import android.content.SharedPreferences;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/8/17
 */
public class LoginManager {
    private static LoginManager INSTANCE;
    private SharedPreferences preferences;

    private static final String USER_ID_KEY = "userId";
    private static final String TOKEN_KEY = "token";

    public static void init(SharedPreferences sharedPreferences) {
        INSTANCE = new LoginManager(sharedPreferences);
    }

    public static LoginManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Call `LoginManager.init(SharedPreferences)` before calling this method.");
        }
        return INSTANCE;
    }

    private LoginManager(SharedPreferences sharedPreferences) {
        this.preferences = sharedPreferences;
    }

    public Long getLoggedInUserId() {
        return preferences.getLong(USER_ID_KEY, -1);
    }

    public void setLoggedInUserId(Long userId) {
        preferences.edit().putLong(USER_ID_KEY, userId).apply();
    }

    public boolean isUserLoggedIn() {
        return getLoggedInUserId() != -1;
    }

    public String getToken() {
        return preferences.getString(TOKEN_KEY, null);
    }

    public void setToken(String token) {
        preferences.edit().putString(TOKEN_KEY, token).apply();
    }
}
