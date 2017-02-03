package com.mastercard.labs.mpqrpayment.data.model;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/3/17
 */
public class Receipt {
    private String merchantName;
    private String merchantCity;
    private Double amount;
    private Double tipAmount;
    private Double totalAmount;
    private String currencyCode;
    private String maskedPan;

    public Receipt(String merchantName, String merchantCity, Double amount, Double tipAmount, Double totalAmount, String currencyCode, String maskedPan) {
        this.merchantName = merchantName;
        this.merchantCity = merchantCity;
        this.amount = amount;
        this.tipAmount = tipAmount;
        this.totalAmount = totalAmount;
        this.currencyCode = currencyCode;
        this.maskedPan = maskedPan;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCity() {
        return merchantCity;
    }

    public void setMerchantCity(String merchantCity) {
        this.merchantCity = merchantCity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(Double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
