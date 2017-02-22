package com.mastercard.labs.mpqrpayment;

import android.app.Application;
import android.content.SharedPreferences;

import com.mastercard.labs.mpqrpayment.network.LoginManager;

import org.apache.commons.lang3.StringUtils;

import io.realm.Realm;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/25/17
 */
public class MainApplication extends Application {
    private static final String SHARED_PREFERENCES_NAME = "MPQRPaymentSharedPreferences";

    public static final String APP_VERSION = String.format("%s (v%s)", StringUtils.capitalize(BuildConfig.FLAVOR), BuildConfig.VERSION_NAME);

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        Realm.init(this);
        LoginManager.init(sharedPreferences);
    }
}
