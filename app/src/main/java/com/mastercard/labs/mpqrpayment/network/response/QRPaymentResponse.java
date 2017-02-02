package com.mastercard.labs.mpqrpayment.network.response;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class QRPaymentResponse {
    private boolean approved;
    private String transactionReference;
    private String transactionDate;

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
}
