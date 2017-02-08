package com.mastercard.labs.mpqrpayment.payment;

import com.mastercard.labs.mpqrpayment.data.DataSource;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.Receipt;
import com.mastercard.labs.mpqrpayment.network.ServiceGenerator;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;
import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;

import java.util.List;
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

    private PaymentData paymentData;
    private PaymentInstrument paymentInstrument;

    PaymentPresenter(PaymentContract.View paymentView, DataSource dataSource, PaymentData paymentData) {
        this.paymentView = paymentView;
        this.dataSource = dataSource;
        this.paymentData = paymentData;
    }

    @Override
    public void start() {

    }

    public void setPaymentData(PaymentData paymentData) {
        this.paymentData = paymentData;

        if (paymentData.isDynamic()) {
            // TODO: Handle dynamic QR
        } else {
            // TODO: Handle static QR
        }

        paymentView.setAmount(paymentData.getTransactionAmount());

        if (paymentData.getTipType() == null) {
            paymentView.hideTipInformation();
        } else {
            paymentView.showTipInformation();
            switch (paymentData.getTipType()) {
                case FLAT_CONVENIENCE_FEE:
                    paymentView.setFlatConvenienceFee(paymentData.getTip());
                    paymentView.disableTipChange();
                    break;
                case PERCENTAGE_CONVENIENCE_FEE:
                    paymentView.setPercentageConvenienceFee(paymentData.getTip());
                    paymentView.disableTipChange();
                    break;
                case PROMPTED_TO_ENTER_TIP:
                    paymentView.setPromptToEnterTip(paymentData.getTip());
                    paymentView.enableTipChange();
                    break;
            }
        }

        CurrencyCode currencyCode = paymentData.getCurrencyCode();
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
        paymentData.setCardId(cardId);

        paymentInstrument = dataSource.getCard(paymentData.getUserId(), cardId);
        if (paymentInstrument == null) {
            // TODO: Show error
            return;
        }

        paymentView.setCard(paymentInstrument);
    }

    @Override
    public void setAmount(double amount) {
        paymentData.setTransactionAmount(amount);

        updateTotal();
    }

    @Override
    public void setTip(double tipAmount) {
        if (!PaymentData.TipInfo.PROMPTED_TO_ENTER_TIP.equals(paymentData.getTipType())) {

            // TODO: Show error as tip change is not allowed
            return;
        }

        paymentData.setTip(tipAmount);

        updateTotal();
    }

    @Override
    public void setCurrencyCode(String currencyCode) {
        if (currencyCode != null) {
            paymentData.setTransactionCurrencyCode(currencyCode);
            updateTotal();
        } else {
            // TODO: Show error
        }
    }

    private void updateTotal() {
        double total = paymentData.getTotal();

        paymentView.setTotalAmount(total, paymentData.getCurrencyCode().toString());
    }

    @Override
    public void selectCard() {
        List<PaymentInstrument> paymentInstruments = dataSource.getCards(paymentData.getUserId());
        paymentView.showCardSelection(paymentInstruments, paymentInstruments.indexOf(paymentInstrument));
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
        ServiceGenerator.getInstance().mpqrPaymentService().makePayment(new PaymentRequest()).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                paymentView.hideProcessingPaymentLoading();
                PaymentResponse paymentResponse = response.body();
                if (paymentResponse.isApproved()) {
                    Double tipAmount = null;
                    if (paymentData.getTipType() != null) {
                        tipAmount = paymentData.getTipAmount();
                    }

                    Receipt receipt = new Receipt(paymentData.getMerchantName(), paymentData.getMerchantCity(), paymentData.getTransactionAmount(), tipAmount, paymentData.getTotal(), paymentData.getCurrencyCode().toString(), paymentInstrument.getMaskedIdentifier());

                    paymentView.showReceipt(receipt);
                } else {
                    // TODO: Show error
                    paymentView.showPaymentFailedError();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                // TODO: Show error
                paymentView.showPaymentFailedError();
            }
        });
    }
}
