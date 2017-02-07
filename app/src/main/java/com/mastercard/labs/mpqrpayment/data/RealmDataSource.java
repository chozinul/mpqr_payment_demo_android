package com.mastercard.labs.mpqrpayment.data;

import com.mastercard.labs.mpqrpayment.data.model.Card;
import com.mastercard.labs.mpqrpayment.data.model.User;

import java.util.List;
import java.util.UUID;

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
    public User saveUser(User user) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();

            User dbUser = realm.where(User.class).equalTo("userId", user.getUserId()).findFirst();
            if (dbUser == null) {
                user.setId(UUID.randomUUID().toString());
            } else {
                user.setId(dbUser.getId());
            }

            user = realm.copyToRealmOrUpdate(user);

            realm.commitTransaction();

            return realm.copyFromRealm(user);
        }
    }

    @Override
    public Card saveCard(Card card) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();

            Card dbCard = realm.where(Card.class).equalTo("cardId", card.getCardId()).findFirst();
            if (dbCard == null) {
                card.setId(UUID.randomUUID().toString());
            } else {
                card.setId(dbCard.getId());
            }

            card = realm.copyToRealmOrUpdate(card);

            realm.commitTransaction();

            return realm.copyFromRealm(card);
        }
    }

    @Override
    public User getUser(Long userId) {
        if (userId == null) {
            return null;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            User user = realm.where(User.class).equalTo("userId", userId).findFirst();
            if (user == null) {
                return null;
            } else {
                return realm.copyFromRealm(user);
            }
        }
    }


    @Override
    public Card getCard(Long userId, Long cardId) {
        if (cardId == null) {
            return null;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            Card card = realm.where(Card.class).equalTo("cardId", cardId).findFirst();
            if (card == null) {
                return null;
            } else {
                return realm.copyFromRealm(card);
            }
        }
    }

    @Override
    public List<Card> getCards(Long userId) {
        if (userId == null) {
            return null;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            User user = realm.where(User.class).equalTo("userId", userId).findFirst();
            if (user == null) {
                return null;
            } else {
                return realm.copyFromRealm(user.getCards());
            }
        }
    }
}
