package com.mastercard.labs.mpqrpayment.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class User extends RealmObject {
    @PrimaryKey
    private long id;
    private String firstName;
    private String lastName;
    private RealmList<PaymentInstrument> paymentInstruments;
    private RealmList<Transaction> transactions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public RealmList<PaymentInstrument> getPaymentInstruments() {
        return paymentInstruments;
    }

    public void setPaymentInstruments(RealmList<PaymentInstrument> paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
    }

    public RealmList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(RealmList<Transaction> transactions) {
        this.transactions = transactions;
    }

}
