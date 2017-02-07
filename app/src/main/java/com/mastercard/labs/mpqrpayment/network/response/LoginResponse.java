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

    static class PaymentInstrument {
        private String acquirerName;
        private String issuerName;
        private String name;
        private String methodType;
        private BigDecimal balance;
        private String maskedIdentifier;
        private String currencyNumericCode;

        public PaymentInstrument(String acquirerName, String issuerName, String name, String methodType, BigDecimal balance, String maskedIdentifier, String currencyNumericCode) {
            this.acquirerName = acquirerName;
            this.issuerName = issuerName;
            this.name = name;
            this.methodType = methodType;
            this.balance = balance;
            this.maskedIdentifier = maskedIdentifier;
            this.currencyNumericCode = currencyNumericCode;
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

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
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
