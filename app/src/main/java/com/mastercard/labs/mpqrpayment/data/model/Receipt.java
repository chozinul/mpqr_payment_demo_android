package com.mastercard.labs.mpqrpayment.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/3/17
 */
public class Receipt implements Parcelable {
    private String merchantName;
    private String merchantCity;
    private Double amount;
    private Double tipAmount;
    private Double totalAmount;
    private String currencyCode;
    private String maskedPan;
    private String methodType;

    public Receipt(String merchantName, String merchantCity, Double amount, Double tipAmount, Double totalAmount, String currencyCode, String maskedPan, String methodType) {
        this.merchantName = merchantName;
        this.merchantCity = merchantCity;
        this.amount = amount;
        this.tipAmount = tipAmount;
        this.totalAmount = totalAmount;
        this.currencyCode = currencyCode;
        this.maskedPan = maskedPan;
        this.methodType = methodType;
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

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.merchantName);
        dest.writeString(this.merchantCity);
        dest.writeValue(this.amount);
        dest.writeValue(this.tipAmount);
        dest.writeValue(this.totalAmount);
        dest.writeString(this.currencyCode);
        dest.writeString(this.maskedPan);
        dest.writeString(this.methodType);
    }

    protected Receipt(Parcel in) {
        this.merchantName = in.readString();
        this.merchantCity = in.readString();
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.tipAmount = (Double) in.readValue(Double.class.getClassLoader());
        this.totalAmount = (Double) in.readValue(Double.class.getClassLoader());
        this.currencyCode = in.readString();
        this.maskedPan = in.readString();
        this.methodType = in.readString();
    }

    public static final Parcelable.Creator<Receipt> CREATOR = new Parcelable.Creator<Receipt>() {
        @Override
        public Receipt createFromParcel(Parcel source) {
            return new Receipt(source);
        }

        @Override
        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };
}
