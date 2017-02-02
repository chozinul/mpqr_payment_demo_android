package com.mastercard.labs.mpqrpayment.database;

import com.mastercard.labs.mpqrpayment.database.model.Card;
import com.mastercard.labs.mpqrpayment.database.model.User;

import io.realm.Realm;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class RealmDataSource implements DataSource {
    private static RealmDataSource dataSource = new RealmDataSource();

    // Prevent direct instantiation.
    private RealmDataSource() {

    }

    public static RealmDataSource getInstance() {
        return dataSource;
    }

    @Override
    public User getUser(Long userId) {
        if (userId == null) {
            return null;
        }

        Realm realm = Realm.getDefaultInstance();

        return realm.where(User.class).equalTo("userId", userId).findFirst();
    }


    @Override
    public Card getCard(Long cardId) {
        if (cardId == null) {
            return null;
        }

        Realm realm = Realm.getDefaultInstance();

        return realm.where(Card.class).equalTo("cardId", cardId).findFirst();
    }
}
