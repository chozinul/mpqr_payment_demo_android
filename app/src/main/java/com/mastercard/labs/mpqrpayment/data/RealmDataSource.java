package com.mastercard.labs.mpqrpayment.data;

import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.User;

import java.util.List;

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

            user = realm.copyToRealmOrUpdate(user);

            realm.commitTransaction();

            return realm.copyFromRealm(user);
        }
    }

    @Override
    public PaymentInstrument saveCard(PaymentInstrument paymentInstrument) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();

            paymentInstrument = realm.copyToRealmOrUpdate(paymentInstrument);

            realm.commitTransaction();

            return realm.copyFromRealm(paymentInstrument);
        }
    }

    @Override
    public User getUser(Long userId) {
        if (userId == null) {
            return null;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            User user = realm.where(User.class).equalTo("id", userId).findFirst();
            if (user == null) {
                return null;
            } else {
                return realm.copyFromRealm(user);
            }
        }
    }


    @Override
    public PaymentInstrument getCard(Long cardId) {
        if (cardId == null) {
            return null;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            PaymentInstrument paymentInstrument = realm.where(PaymentInstrument.class).equalTo("id", cardId).findFirst();
            if (paymentInstrument == null) {
                return null;
            } else {
                return realm.copyFromRealm(paymentInstrument);
            }
        }
    }

    @Override
    public List<PaymentInstrument> getCards(Long userId) {
        if (userId == null) {
            return null;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            User user = realm.where(User.class).equalTo("id", userId).findFirst();
            if (user == null) {
                return null;
            } else {
                return realm.copyFromRealm(user.getPaymentInstruments());
            }
        }
    }

    @Override
    public boolean deleteUser(long id) {
        try (Realm realm = Realm.getDefaultInstance()) {
            User user = realm.where(User.class).equalTo("id", id).findFirst();
            if (user == null) {
                return false;
            }

            realm.beginTransaction();

            user.getPaymentInstruments().deleteAllFromRealm();
            user.deleteFromRealm();

            realm.commitTransaction();
        }
        return true;
    }

}

