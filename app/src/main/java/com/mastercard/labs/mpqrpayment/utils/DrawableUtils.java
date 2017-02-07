package com.mastercard.labs.mpqrpayment.utils;

import android.support.annotation.DrawableRes;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.Card;
import com.mastercard.labs.mpqrpayment.data.model.MethodType;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/7/17
 */
public class DrawableUtils {
    public static @DrawableRes int getPaymentMethodLogo(Card card) {
        MethodType methodType = MethodType.fromString(card.getMethodType());

        @DrawableRes int imageId = 0;
        switch (methodType) {
            case CreditCard:
            case DebitCard:
                if (card.getName().toLowerCase().contains("mastercard")) {
                    imageId = R.drawable.mastercard_logo;
                }
                break;
            case SavingsAccount:
                imageId = R.drawable.savings_account_logo;
            default:
                // TODO: Add default card logo
                break;
        }

        return imageId;
    }

    public static @DrawableRes int getPaymentMethodImage(Card card) {
        MethodType methodType = MethodType.fromString(card.getMethodType());

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
                // TODO: Add default card image
                break;
        }

        return imageId;
    }
}
