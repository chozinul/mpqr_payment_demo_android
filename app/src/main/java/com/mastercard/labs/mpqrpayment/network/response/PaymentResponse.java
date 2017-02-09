package com.mastercard.labs.mpqrpayment.network.response;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class PaymentResponse {
    private boolean approved;
    private String transactionReference;
    private String transactionDate;
    private String message;
    private double transactionAmount;

    public PaymentResponse(boolean approved, String transactionReference, String transactionDate, String message, double transactionAmount) {
        this.approved = approved;
        this.transactionReference = transactionReference;
        this.transactionDate = transactionDate;
        this.message = message;
        this.transactionAmount = transactionAmount;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public boolean isInsufficientBalance() {
        return message != null && message.equalsIgnoreCase("insufficient_balance");

    }
}
