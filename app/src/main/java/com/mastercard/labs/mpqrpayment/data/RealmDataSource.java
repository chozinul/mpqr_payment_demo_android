package com.mastercard.labs.mpqrpayment.data;

import android.util.Log;

import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.Transaction;
import com.mastercard.labs.mpqrpayment.data.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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

    @Override
    public Transaction getTransaction(String referenceId) {
        if (referenceId == null) {
            return null;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            Transaction transaction = realm.where(Transaction.class).equalTo("referenceId", referenceId).findFirst();
            if (transaction == null) {
                return null;
            } else {
                return realm.copyFromRealm(transaction);
            }
        }
    }

    @Override
    public List<Transaction> getTransactions(long userId, String maskedIdentifier) {
        try (Realm realm = Realm.getDefaultInstance()) {
            User user = realm.where(User.class).equalTo("id", userId).findFirst();
            if (user == null) {
                return new ArrayList<>();
            }

            RealmResults<Transaction> results = user.getTransactions().where().equalTo("maskedIdentifier", maskedIdentifier).findAllSorted("transactionDate", Sort.DESCENDING);

            return realm.copyFromRealm(results);
        }
    }

    @Override
    public Transaction saveTransaction(long userId, Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            User user = realm.where(User.class).equalTo("id", userId).findFirst();
            if (user == null) {
                return null;
            }

            realm.beginTransaction();

            transaction = realm.copyToRealmOrUpdate(transaction);

            Set<Transaction> transactions = new HashSet<>(user.getTransactions());
            transactions.add(transaction);

            user.getTransactions().clear();
            user.getTransactions().addAll(transactions);

            realm.copyToRealmOrUpdate(user);

            realm.commitTransaction();

            return realm.copyFromRealm(transaction);
        }
    }

    public List<Transaction> getAllTransactions(long userId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            User user = realm.where(User.class).equalTo("id", userId).findFirst();
            if (user == null) {
                return new ArrayList<>();
            }

            return realm.copyFromRealm(user.getTransactions());
        }
    }
}

