package com.mastercard.labs.mpqrpayment.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/25/17
 */
public class Card extends RealmObject {
    @PrimaryKey
    private Long cardId;

    // TODO: Should add masked pan or not?

    private String cardNumber;
    private String bankName;
    private String cardType;
    private String currencyNumericCode;
    private Double balance;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public CardType getCardType() {
        return CardType.valueOf(cardType);
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType.name();
    }

    public String getCurrencyNumericCode() {
        return currencyNumericCode;
    }

    public void setCurrencyNumericCode(String currencyCode) {
        this.currencyNumericCode = currencyCode;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getMaskedPan() {
        return "**** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
