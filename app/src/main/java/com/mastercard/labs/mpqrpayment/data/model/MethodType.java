package com.mastercard.labs.mpqrpayment.data.model;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/25/17
 */
public enum MethodType {
    CreditCard("CreditCard"),
    DebitCard("DebitCard"),
    SavingsAccount("SavingsAccount"),
    Unknown("Unknown");

    private String methodType;

    MethodType(String text) {
        this.methodType = text;
    }

    public String getMethodType() {
        return this.methodType;
    }

    public static MethodType fromString(String text) {
        if (text != null) {
            for (MethodType value : MethodType.values()) {
                if (text.equalsIgnoreCase(value.methodType)) {
                    return value;
                }
            }
        }
        return Unknown;
    }
}
