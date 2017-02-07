package com.mastercard.labs.mpqrpayment;

import android.app.Application;
import android.content.SharedPreferences;

import com.mastercard.labs.mpqrpayment.data.model.Card;
import com.mastercard.labs.mpqrpayment.data.model.MethodType;
import com.mastercard.labs.mpqrpayment.data.model.User;

import java.util.Arrays;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/25/17
 */
public class MainApplication extends Application {
    private static String sharedPreferencesName = "MPQRPaymentSharedPreferences";
    private static SharedPreferences sharedPreferences;

    private static String SP_USER_ID_KEY = "userId";

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);

        Realm.init(this);
    }

    public static boolean isUserLoggedIn() {
        return loggedInUserId() != -1;
    }

    public static void setLoggedInUserId(Long userLoggedInId) {
        sharedPreferences.edit().putLong(SP_USER_ID_KEY, userLoggedInId).apply();
    }

    public static Long loggedInUserId() {
        return sharedPreferences.getLong(SP_USER_ID_KEY, -1);
    }
}
