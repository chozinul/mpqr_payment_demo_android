package com.mastercard.labs.mpqrpayment.data.model;

import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;

import java.util.Date;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kaile on 17/7/17.
 */

public class Transaction extends RealmObject {

    @PrimaryKey
    public String referenceId;

    public String invoiceNumber;
    public String maskedIdentifier;
    public double transactionAmount;
    public double tipAmount;
    public String currencyNumericCode;
    public Date transactionDate;
    public String merchantName;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getMaskedIdentifier() {
        return maskedIdentifier;
    }

    public void setMaskedIdentifier(String cardNo) {
        this.maskedIdentifier = cardNo;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }

    public double getTotal() {
        return transactionAmount + getTipAmount();
    }

    public String getCurrencyNumericCode() {
        return currencyNumericCode;
    }

    public void setCurrencyNumericCode(String currencyNumericCode) {
        this.currencyNumericCode = currencyNumericCode;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.fromNumericCode(currencyNumericCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.transactionAmount, transactionAmount) == 0 &&
                Double.compare(that.tipAmount, tipAmount) == 0 &&
                Objects.equals(merchantName, that.merchantName) &&
                Objects.equals(currencyNumericCode, that.currencyNumericCode) &&
                Objects.equals(transactionDate, that.transactionDate) &&
                Objects.equals(invoiceNumber, that.invoiceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionAmount, tipAmount, currencyNumericCode, transactionDate, invoiceNumber, merchantName);
    }
}
