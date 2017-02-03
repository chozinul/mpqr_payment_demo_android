package com.mastercard.labs.mpqrpayment.payment;

import com.mastercard.labs.mpqrpayment.data.DataSource;
import com.mastercard.labs.mpqrpayment.data.model.Card;
import com.mastercard.labs.mpqrpayment.data.model.Receipt;
import com.mastercard.labs.mpqrpayment.network.ServiceGenerator;
import com.mastercard.labs.mpqrpayment.network.request.QRPaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.QRPaymentResponse;
import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.parser.Parser;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/1/17
 */
class PaymentPresenter implements PaymentContract.Presenter {
    private static int PIN_SIZE = 6;
    private static Pattern pinPattern = Pattern.compile("[0-9]{" + PIN_SIZE + "}");

    private PaymentContract.View paymentView;
    private DataSource dataSource;
    private Long userId;

    private PushPaymentData paymentData;
    private Card card;

    private CurrencyCode currencyCode;
    private double amount;
    private double tip;

    PaymentPresenter(PaymentContract.View paymentView, DataSource dataSource, Long userId) {
        this.paymentView = paymentView;
        this.dataSource = dataSource;
        this.userId = userId;
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
        tip = 0;

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
    }

    @Override
    public void setCardId(Long cardId) {
        card = dataSource.getCard(userId, cardId);
        if (card == null) {
            // TODO: Show error
            return;
        }

        paymentView.setCard(card);
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

    private double getTipAmount() {
        if (PushPaymentData.TipConvenienceIndicator.PERCENTAGE_CONVENIENCE_FEE.equals(paymentData.getTipOrConvenienceIndicator())) {
            return amount * tip / 100;
        } else {
            return tip;
        }
    }

    private void updateTotal() {
        double total = amount + getTipAmount();

        paymentView.setTotalAmount(total, currencyCode.toString());
    }

    @Override
    public void selectCard() {
        List<Card> cards = dataSource.getCards(userId);
        paymentView.showCardSelection(cards, cards.indexOf(card));
    }

    @Override
    public void makePayment() {
        paymentView.askPin(PIN_SIZE);
    }

    @Override
    public void pin(String pin) {
        Matcher matcher = pinPattern.matcher(pin);
        if (!matcher.matches()) {
            paymentView.showInvalidPinError();
            return;
        }

        paymentView.showProcessingPaymentLoading();

        // TODO: Validate pin on server

        // TODO: Process payment
        ServiceGenerator.getInstance().mpqrPaymentService().makePayment(new QRPaymentRequest()).enqueue(new Callback<QRPaymentResponse>() {
            @Override
            public void onResponse(Call<QRPaymentResponse> call, Response<QRPaymentResponse> response) {
                paymentView.hideProcessingPaymentLoading();
                QRPaymentResponse paymentResponse = response.body();
                if (paymentResponse.isApproved()) {
                    Receipt receipt = new Receipt();
                    receipt.setMerchantName(paymentData.getMerchantName());
                    receipt.setMerchantCity(paymentData.getMerchantCity());
                    receipt.setAmount(amount);
                    receipt.setCurrencyCode(currencyCode.toString());
                    receipt.setMaskedPan(card.getMaskedPan());

                    if (paymentData.getTipOrConvenienceIndicator() != null) {
                        receipt.setTip(getTipAmount());
                    }

                    paymentView.showReceipt(receipt);
                } else {
                    // TODO: Show error
                    paymentView.showPaymentFailedError();
                }
            }

            @Override
            public void onFailure(Call<QRPaymentResponse> call, Throwable t) {
                // TODO: Show error
                paymentView.showPaymentFailedError();
            }
        });
    }
}
