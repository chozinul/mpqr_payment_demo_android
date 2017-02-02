package com.mastercard.labs.mpqrpayment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.mastercard.labs.mpqrpayment.database.model.Card;
import com.mastercard.labs.mpqrpayment.database.model.CardType;
import com.mastercard.labs.mpqrpayment.database.model.User;

import java.util.Arrays;

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

        if (BuildConfig.DEBUG) {
            setupDebugData();
        }
    }

    void setupDebugData() {
        Card card1 = new Card();
        card1.setCardId(1L);
        card1.setBankName("Ecobank");
        card1.setCardNumber("5184680430000006");
        card1.setCardType(CardType.MastercardGold);
        card1.setCurrencyNumericCode("356");
        card1.setBalance(120.90);

        Card card2 = new Card();
        card2.setCardId(2L);
        card2.setBankName("Ecobank");
        card2.setCardNumber("5105105105105101");
        card2.setCardType(CardType.MastercardBlack);
        card2.setCurrencyNumericCode("356");
        card2.setBalance(5100.20);

        Card card3 = new Card();
        card3.setCardId(3L);
        card3.setBankName("Ecobank");
        card3.setCardNumber("5105105105105102");
        card3.setCardType(CardType.SavingsAccount);
        card3.setCurrencyNumericCode("356");
        card3.setBalance(21370.00);

        User user = new User();
        user.setUserId(1L);
        user.setFirstName("Thomas");
        user.setLastName("Wilson");
        user.setAddressLine1("42 ELM AVENUE");
        user.setAddressCity("ANYTOWN");
        user.setAddressState("MO");
        user.setAddressCountry("USA");
        user.setCards(new RealmList<>(card1, card2, card3));
        user.setDefaultCard(card1);

        RealmConfiguration configuration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(configuration);
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();

            realm.copyToRealmOrUpdate(Arrays.asList(card1, card2, card3));
            realm.copyToRealmOrUpdate(user);

            realm.commitTransaction();
        }

        sharedPreferences.edit().putLong(SP_USER_ID_KEY, user.getUserId()).apply();
    }

    public static Long loggedInUserId() {
        return sharedPreferences.getLong(SP_USER_ID_KEY, -1);
    }
}
