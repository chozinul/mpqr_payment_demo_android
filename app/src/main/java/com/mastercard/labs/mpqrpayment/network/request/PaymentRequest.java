package com.mastercard.labs.mpqrpayment.network.request;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class PaymentRequest {
    private String receiverCardNumber;
    private Long senderCardId;
    private String currency;
    private double amountInCents;

    public PaymentRequest(String receiverCardNumber, Long senderCardId, String currency, double amountInCents) {
        this.receiverCardNumber = receiverCardNumber;
        this.senderCardId = senderCardId;
        this.currency = currency;
        this.amountInCents = amountInCents;
    }

    public String getReceiverCardNumber() {
        return receiverCardNumber;
    }

    public void setReceiverCardNumber(String receiverCardNumber) {
        this.receiverCardNumber = receiverCardNumber;
    }

    public Long getSenderCardId() {
        return senderCardId;
    }

    public void setSenderCardId(Long senderCardId) {
        this.senderCardId = senderCardId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getTransactionAmount() {
        return amountInCents;
    }

    public void setAmountInCents(double amountInCents) {
        this.amountInCents = amountInCents;
    }
}
