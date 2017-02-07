package com.mastercard.labs.mpqrpayment.network.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/7/17
 */
public class LoginResponse {
    private long id;
    private String firstName;
    private String lastName;

    private List<PaymentInstrument> paymentInstruments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<PaymentInstrument> getPaymentInstruments() {
        return paymentInstruments;
    }

    public void setPaymentInstruments(List<PaymentInstrument> paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
    }

    public static class PaymentInstrument {
        private long id;
        private String acquirerName;
        private String issuerName;
        private String name;
        private String methodType;
        private Double balance;
        private String maskedIdentifier;
        private String currencyNumericCode;

        public PaymentInstrument(long id, String acquirerName, String issuerName, String name, String methodType, Double balance, String maskedIdentifier, String currencyNumericCode) {
            this.id = id;
            this.acquirerName = acquirerName;
            this.issuerName = issuerName;
            this.name = name;
            this.methodType = methodType;
            this.balance = balance;
            this.maskedIdentifier = maskedIdentifier;
            this.currencyNumericCode = currencyNumericCode;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getAcquirerName() {
            return acquirerName;
        }

        public void setAcquirerName(String acquirerName) {
            this.acquirerName = acquirerName;
        }

        public String getIssuerName() {
            return issuerName;
        }

        public void setIssuerName(String issuerName) {
            this.issuerName = issuerName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMethodType() {
            return methodType;
        }

        public void setMethodType(String methodType) {
            this.methodType = methodType;
        }

        public Double getBalance() {
            return balance;
        }

        public void setBalance(Double balance) {
            this.balance = balance;
        }

        public String getMaskedIdentifier() {
            return maskedIdentifier;
        }

        public void setMaskedIdentifier(String maskedIdentifier) {
            this.maskedIdentifier = maskedIdentifier;
        }

        public String getCurrencyNumericCode() {
            return currencyNumericCode;
        }

        public void setCurrencyNumericCode(String currencyNumericCode) {
            this.currencyNumericCode = currencyNumericCode;
        }
    }
}
