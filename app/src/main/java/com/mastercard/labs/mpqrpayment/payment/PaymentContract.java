package com.mastercard.labs.mpqrpayment.payment;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;
import com.mastercard.labs.mpqrpayment.data.model.Card;
import com.mastercard.labs.mpqrpayment.data.model.Receipt;

import java.util.List;

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

        void setCard(Card card);

        void showCardSelection(List<Card> cards, int selectedCardIdx);

        void askPin(int pinLength);

        void showProcessingPaymentLoading();

        void hideProcessingPaymentLoading();

        void showReceipt(Receipt receipt);

        void showPaymentFailedError();

        void showInvalidPinError();
    }

    interface Presenter extends BasePresenter {

        void setPushPaymentDataString(String pushPaymentDataString);

        void setCardId(Long cardId);

        void setAmount(double amount);

        void setTip(double tipAmount);

        void setCurrencyCode(String currencyCode);

        void selectCard();

        void makePayment();

        void pin(String pin);
    }
}
