package com.mastercard.labs.mpqrpayment.network.request;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class PaymentRequest {
    private String receiverCardNumber;
    private Long senderCardId;
    private String currency;
    private double transactionAmount;
    private double tip;
    private String terminalNumber;

    public PaymentRequest(String receiverCardNumber, Long senderCardId, String currency, double transactionAmount, double tip, String terminalNumber) {
        this.receiverCardNumber = receiverCardNumber;
        this.senderCardId = senderCardId;
        this.currency = currency;
        this.transactionAmount = transactionAmount;
        this.tip = tip;
        this.terminalNumber = terminalNumber;
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
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public double getTotal() {
        return getTransactionAmount() + getTip();
    }

    public String getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }
}
