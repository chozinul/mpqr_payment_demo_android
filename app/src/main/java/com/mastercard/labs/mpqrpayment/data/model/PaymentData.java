package com.mastercard.labs.mpqrpayment.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/8/17
 */
public class PaymentData implements Parcelable {
    private long userId;
    private long cardId;
    private boolean isDynamic;
    private double transactionAmount;
    private TipInfo tipType;
    private double tip;
    private String transactionCurrencyCode;
    private Merchant merchant;

    public PaymentData(long userId, long cardId, boolean isDynamic, double transactionAmount, TipInfo tipType, double tip, String transactionCurrencyCode, Merchant merchant) {
        this.userId = userId;
        this.cardId = cardId;
        this.isDynamic = isDynamic;
        this.transactionAmount = transactionAmount;
        this.tipType = tipType;
        this.tip = tip;
        this.transactionCurrencyCode = transactionCurrencyCode;
        this.merchant = merchant;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public TipInfo getTipType() {
        return tipType;
    }

    public void setTipType(TipInfo tipType) {
        this.tipType = tipType;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public String getTransactionCurrencyCode() {
        return transactionCurrencyCode;
    }

    public void setTransactionCurrencyCode(String transactionCurrencyCode) {
        this.transactionCurrencyCode = transactionCurrencyCode;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public double getTipAmount() {
        if (PaymentData.TipInfo.PERCENTAGE_CONVENIENCE_FEE.equals(tipType)) {
            return transactionAmount * tip / 100;
        } else {
            return tip;
        }
    }

    public double getTotal() {
        return transactionAmount + getTipAmount();
    }

    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.fromNumericCode(transactionCurrencyCode);
    }

    public enum TipInfo {
        PROMPTED_TO_ENTER_TIP,
        FLAT_CONVENIENCE_FEE,
        PERCENTAGE_CONVENIENCE_FEE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeLong(this.cardId);
        dest.writeByte(this.isDynamic ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.transactionAmount);
        dest.writeInt(this.tipType == null ? -1 : this.tipType.ordinal());
        dest.writeDouble(this.tip);
        dest.writeString(this.transactionCurrencyCode);
        dest.writeParcelable(this.merchant, flags);
    }

    protected PaymentData(Parcel in) {
        this.userId = in.readLong();
        this.cardId = in.readLong();
        this.isDynamic = in.readByte() != 0;
        this.transactionAmount = in.readDouble();
        int tmpTipType = in.readInt();
        this.tipType = tmpTipType == -1 ? null : TipInfo.values()[tmpTipType];
        this.tip = in.readDouble();
        this.transactionCurrencyCode = in.readString();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    public static final Creator<PaymentData> CREATOR = new Creator<PaymentData>() {
        @Override
        public PaymentData createFromParcel(Parcel source) {
            return new PaymentData(source);
        }

        @Override
        public PaymentData[] newArray(int size) {
            return new PaymentData[size];
        }
    };
}
