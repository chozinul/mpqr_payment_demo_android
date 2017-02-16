package com.mastercard.labs.mpqrpayment.network.response;

import java.util.Date;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class PaymentResponse {
    private boolean approved;
    private String transactionReference;
    private Date transactionDate;
    private String invoiceNumber;
    private String message;
    private double totalAmount;

    public PaymentResponse(boolean approved, String transactionReference, Date transactionDate, String invoiceNumber, String message, double totalAmount) {
        this.approved = approved;
        this.transactionReference = transactionReference;
        this.transactionDate = transactionDate;
        this.invoiceNumber = invoiceNumber;
        this.message = message;
        this.totalAmount = totalAmount;
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

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isInsufficientBalance() {
        return message != null && message.equalsIgnoreCase("insufficient_balance");

    }
}
