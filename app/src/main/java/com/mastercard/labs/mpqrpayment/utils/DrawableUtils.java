package com.mastercard.labs.mpqrpayment.utils;

import android.support.annotation.DrawableRes;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.MethodType;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/7/17
 */
public class DrawableUtils {
    public static @DrawableRes int getPaymentMethodLogo(PaymentInstrument paymentInstrument) {
        MethodType methodType = MethodType.fromString(paymentInstrument.getMethodType());

        @DrawableRes int imageId = 0;
        switch (methodType) {
            case CreditCard:
                imageId = R.drawable.method_credit_card_logo;
                break;
            case DebitCard:
                imageId = R.drawable.method_debit_card_logo;
                break;
            case SavingsAccount:
                imageId = R.drawable.method_savings_account_logo;
            default:
                // TODO: Add default paymentInstrument logo
                break;
        }

        return imageId;
    }

    public static @DrawableRes int getPaymentMethodImage(PaymentInstrument paymentInstrument) {
        MethodType methodType = MethodType.fromString(paymentInstrument.getMethodType());

        @DrawableRes int imageId = 0;
        switch (methodType) {
            case CreditCard:
                imageId = R.drawable.mastercard_black;
                break;
            case DebitCard:
                imageId = R.drawable.mastercard_gold;
                break;
            case SavingsAccount:
                imageId = R.drawable.saving_account;
                break;
            default:
                // TODO: Add default paymentInstrument image
                break;
        }

        return imageId;
    }
}
