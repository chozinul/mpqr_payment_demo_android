package com.mastercard.labs.mpqrpayment.payment;

import com.mastercard.labs.mpqrpayment.database.model.Card;
import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.parser.Parser;

import java.util.Currency;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/1/17
 */
class PaymentPresenter implements PaymentContract.Presenter {
    private PushPaymentData paymentData;
    private Card cardId;
    private PaymentContract.View paymentView;

    private CurrencyCode currencyCode;
    private double amount;
    private double tip;

    PaymentPresenter(PaymentContract.View paymentView) {
        this.paymentView = paymentView;
    }

    @Override
    public void start() {
    }

    @Override
    public void setPushPaymentDataString(String pushPaymentDataString) {
        if (pushPaymentDataString != null) {
            try {
                paymentData = Parser.parse(pushPaymentDataString);
            } catch (FormatException e) {
                e.printStackTrace();

                // TODO: Show error
                return;
            }
        } else {
            // TODO: Handle empty payment data
            return;
        }

        if (paymentData.isDynamic()) {
            // TODO: Handle dynamic QR
        } else {
            // TODO: Handle static QR
        }

        amount = paymentData.getTransactionAmount();

        paymentView.setAmount(amount);

        if (paymentData.getTipOrConvenienceIndicator() == null) {
            paymentView.hideTipInformation();
        } else {
            paymentView.showTipInformation();
            switch (paymentData.getTipOrConvenienceIndicator()) {
                case PushPaymentData.TipConvenienceIndicator.FLAT_CONVENIENCE_FEE:
                    tip = paymentData.getValueOfConvenienceFeeFixed();

                    paymentView.setFlatConvenienceFee(tip);
                    paymentView.disableTipChange();
                    break;
                case PushPaymentData.TipConvenienceIndicator.PERCENTAGE_CONVENIENCE_FEE:
                    tip = paymentData.getValueOfConvenienceFeePercentage();

                    paymentView.setPercentageConvenienceFee(tip);
                    paymentView.disableTipChange();
                    break;
                case PushPaymentData.TipConvenienceIndicator.PROMTED_TO_ENTER_TIP:
                    tip = 0;

                    paymentView.setPromptToEnterTip(tip);
                    paymentView.enableTipChange();
                    break;
            }
        }

        currencyCode = CurrencyCode.fromNumericCode(paymentData.getTransactionCurrencyCode());
        if (currencyCode != null) {
            paymentView.setCurrency(currencyCode.toString());
        } else {
            // TODO: Show error
        }

        paymentView.setMerchantName(paymentData.getMerchantName());
        paymentView.setMerchantCity(paymentData.getMerchantCity());

        updateTotal();

        // TODO: Add card info
    }

    @Override
    public void setAmount(double amount) {
        this.amount = amount;

        updateTotal();
    }

    @Override
    public void setTip(double tipAmount) {
        if (!PushPaymentData.TipConvenienceIndicator.PROMTED_TO_ENTER_TIP.equals(paymentData.getTipOrConvenienceIndicator())) {

            // TODO: Show error as tip change is not allowed
            return;
        }

        tip = tipAmount;

        updateTotal();
    }

    @Override
    public void setCurrencyCode(String currencyCode) {
        CurrencyCode currency = CurrencyCode.fromNumericCode(paymentData.getTransactionCurrencyCode());
        if (currencyCode != null) {
            this.currencyCode = currency;

            updateTotal();
        } else {
            // TODO: Show error
        }
    }

    private void updateTotal() {
        double total = 0;

        if (PushPaymentData.TipConvenienceIndicator.PERCENTAGE_CONVENIENCE_FEE.equals(paymentData.getTipOrConvenienceIndicator())) {
            total += amount * tip / 100;
        } else {
            total += amount + tip;
        }

        paymentView.setTotalAmount(total, currencyCode.toString());
    }
}