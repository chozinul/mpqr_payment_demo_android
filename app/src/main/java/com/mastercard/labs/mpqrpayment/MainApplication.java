package com.mastercard.labs.mpqrpayment;

import android.app.Application;
import android.content.SharedPreferences;

import com.mastercard.labs.mpqrpayment.network.LoginManager;

import io.realm.Realm;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/25/17
 */
public class MainApplication extends Application {
    private static final String SHARED_PREFERENCES_NAME = "MPQRPaymentSharedPreferences";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        Realm.init(this);
        LoginManager.init(sharedPreferences);
    }
}
