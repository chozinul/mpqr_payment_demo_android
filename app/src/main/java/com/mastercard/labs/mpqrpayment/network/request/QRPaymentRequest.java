package com.mastercard.labs.mpqrpayment.network.request;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class QRPaymentRequest {
    private String receiverCardNumber;
    private String senderCardNumber;
    private String currency;
    private Long amountInCents;

    public String getReceiverCardNumber() {
        return receiverCardNumber;
    }

    public void setReceiverCardNumber(String receiverCardNumber) {
        this.receiverCardNumber = receiverCardNumber;
    }

    public String getSenderCardNumber() {
        return senderCardNumber;
    }

    public void setSenderCardNumber(String senderCardNumber) {
        this.senderCardNumber = senderCardNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }
}
