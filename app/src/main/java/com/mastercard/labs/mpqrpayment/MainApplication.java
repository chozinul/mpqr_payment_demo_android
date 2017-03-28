package com.mastercard.labs.mpqrpayment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.mastercard.labs.mpqrpayment.network.LoginManager;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

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
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String,?> keys = sharedPref.getAll();

        if (keys.isEmpty()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("55223345","Go Go Transport|5555222233334456|55223345");
            editor.putString("56775486", "Comfort Delgro|5263727556488665|56775486");
            editor.putString("52600925", "Food Merchant|5227620901962259|52600925");
            editor.putString("50523802", "Merchant|5307592733860428|50523802");
            editor.putString("52276336", "Misc Merchant|5320257360393266|52276336");

            editor.commit();
        }

        Realm.init(this);
        LoginManager.init(sharedPreferences);
    }
}
