package com.mastercard.labs.mpqrpayment;

import android.app.Application;

import com.mastercard.labs.mpqrpayment.database.model.Card;
import com.mastercard.labs.mpqrpayment.database.model.CardType;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/25/17
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        if (BuildConfig.DEBUG) {
            setupDebugData();
        }
    }

    void setupDebugData() {
        Card card1 = new Card();
        card1.setBankName("Ecobank");
        card1.setCardNumber("5105105105105100");
        card1.setCardType(CardType.MastercardGold);
        card1.setBalance(120.90);

        Card card2 = new Card();
        card2.setBankName("Ecobank");
        card2.setCardNumber("5105105105105101");
        card2.setCardType(CardType.MastercardBlack);
        card2.setBalance(5100.20);

        Card card3 = new Card();
        card3.setBankName("Ecobank");
        card3.setCardNumber("5105105105105102");
        card3.setCardType(CardType.SavingsAccount);
        card3.setBalance(21370.00);

        RealmConfiguration configuration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(configuration);
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();

            realm.copyToRealmOrUpdate(Arrays.asList(card1, card2, card3));

            realm.commitTransaction();
        }
    }
}
