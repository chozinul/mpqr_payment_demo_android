package com.mastercard.labs.mpqrpayment.payment;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/1/17
 */
public interface PaymentContract {
    interface View extends BaseView<Presenter> {

        void setCurrency(String transactionCurrencyCode);

        void setAmount(double amount);

        void setFlatConvenienceFee(double fee);

        void setPercentageConvenienceFee(double feePercentage);

        void setPromptToEnterTip(double tip);

        void disableTipChange();

        void enableTipChange();

        void setTotalAmount(double amount, String currencyCode);

        void hideTipInformation();

        void showTipInformation();

        void setMerchantName(String merchantName);

        void setMerchantCity(String merchantCity);
    }

    interface Presenter extends BasePresenter {

        void setPushPaymentDataString(String pushPaymentDataString);

        void setAmount(double amount);

        void setTip(double tipAmount);

        void setCurrencyCode(String currencyCode);
    }
}
