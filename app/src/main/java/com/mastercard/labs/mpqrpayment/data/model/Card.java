package com.mastercard.labs.mpqrpayment.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/25/17
 */
public class Card extends RealmObject {
    @PrimaryKey
    private String id;

    private Long cardId;
    private String acquirerName;
    private String issuerName;
    private String name;
    private String methodType;
    private Double balance;
    private String maskedIdentifier;
    private String currencyNumericCode;

    public String getId() {
        return id;
    }

    public void setId(String cardId) {
        this.id = cardId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getAcquirerName() {
        return acquirerName;
    }

    public void setAcquirerName(String acquirerName) {
        this.acquirerName = acquirerName;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getMaskedIdentifier() {
        return maskedIdentifier;
    }

    public void setMaskedIdentifier(String maskedIdentifier) {
        this.maskedIdentifier = maskedIdentifier;
    }

    public String getCurrencyNumericCode() {
        return currencyNumericCode;
    }

    public void setCurrencyNumericCode(String currencyNumericCode) {
        this.currencyNumericCode = currencyNumericCode;
    }
}
