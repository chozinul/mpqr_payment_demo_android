package com.mastercard.labs.mpqrpayment.payment;

import android.app.Activity;
import android.util.Log;

import com.mastercard.labs.mpqrpayment.data.DataSource;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.Receipt;
import com.mastercard.labs.mpqrpayment.data.model.Transaction;
import com.mastercard.labs.mpqrpayment.network.ServiceGenerator;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;
import com.mastercard.labs.mpqrpayment.service.NotificationService;
import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;
import com.mastercard.labs.mpqrpayment.utils.PreferenceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mastercard.labs.mpqrpayment.R.string.amount;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/1/17
 */
class PaymentPresenter implements PaymentContract.Presenter {
    private static int PIN_SIZE = 6;
    private static Pattern pinPattern = Pattern.compile("[0-9]{" + PIN_SIZE + "}");

    private PaymentContract.View paymentView;
    private DataSource dataSource;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private PaymentData paymentData;
    private PaymentInstrument paymentInstrument;

    private Call<PaymentResponse> paymentRequest;

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
            paymentView.disableAmountChange();
        } else {
            paymentView.enableAmountChange();
        }

        if (paymentData.getMerchant() == null) {
            paymentView.showInvalidDataError();
            return;
        }

        // TODO: Validate if merchant has required identifier as the selected card

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
            paymentView.showInvalidDataError();
            return;
        }

        paymentView.setMerchantName(paymentData.getMerchant().getName());
        paymentView.setMerchantCity(paymentData.getMerchant().getCity());

        setCardId(paymentData.getCardId());

        updateTotal();
    }

    @Override
    public void setCardId(Long cardId) {
        paymentData.setCardId(cardId);

        paymentInstrument = dataSource.getCard(cardId);
        if (paymentInstrument == null) {
            paymentView.showInvalidDataError();
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
            paymentView.showTipChangeNotAllowedError();
            return;
        }

        paymentData.setTip(tipAmount);

        updateTotal();
    }

    @Override
    public void setCurrencyCode(String currencyCode) {
        // Assign only if valid numeric code
        if (CurrencyCode.fromNumericCode(currencyCode) != null) {
            paymentData.setCurrencyNumericCode(currencyCode);
            updateTotal();
        } else {
            paymentView.showInvalidDataError();
        }
    }

    private void updateTotal() {
        double total = paymentData.getTotal();

        String currencyCode = "";
        if (paymentData.getCurrencyCode() != null) {
            currencyCode = paymentData.getCurrencyCode().toString();
        }

        paymentView.setTotalAmount(total, currencyCode);
    }

    @Override
    public void selectCard() {
        List<PaymentInstrument> paymentInstruments = dataSource.getCards(paymentData.getUserId());
        paymentView.showCardSelection(paymentInstruments, paymentInstruments.indexOf(paymentInstrument));
    }

    @Override
    public void makePayment() {
        // TODO: Ask for pin before proceeding
        // TODO: Validate payment data before moving forward
//        paymentView.askPin(PIN_SIZE);

        requestPayment();
    }

    @Override
    public void pin(String pin) {
        Matcher matcher = pinPattern.matcher(pin);
        if (!matcher.matches()) {
            paymentView.showInvalidPinError();
            return;
        }

        // TODO: Validate pin on server

        requestPayment();
    }

    @Override
    public void confirmCancellation() {
        paymentView.showCancelDialog();
    }

    @Override
    public void cancelFlow() {
        paymentView.close();
    }

    private void requestPayment() {

        if (paymentRequest != null) {
            paymentRequest.cancel();
        }

        paymentView.showProcessingPaymentLoading();

        // TODO: Pick correct identifier
        final String receiverIdentifier = paymentData.getMerchant().getIdentifierMastercard04();
        final PaymentRequest requestData = new PaymentRequest(receiverIdentifier, paymentData.getCardId(), paymentData.getCurrencyNumericCode(), paymentData.getTransactionAmount(), paymentData.getTipAmount(), paymentData.getMerchant().getTerminalNumber());

        paymentRequest = ServiceGenerator.getInstance().mpqrPaymentService().makePayment(requestData);
        paymentRequest.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                paymentView.hideProcessingPaymentLoading();
                paymentRequest = null;

                if (!response.isSuccessful()) {
                    paymentView.showPaymentFailedError();
                    return;
                }

                PaymentResponse paymentResponse = response.body();
                if (!paymentResponse.isApproved()) {
                    if (paymentResponse.isInsufficientBalance()) {
                        paymentView.showInsufficientBalanceError();
                    } else {
                        paymentView.showPaymentFailedError();
                    }
                    return;
                }

                // Send notification

                final Map<String, Object> message = new HashMap<>();
                message.put("transactionAmount", requestData.getTransactionAmount());
                message.put("tipAmount", requestData.getTip());
                message.put("currencyNumericCode", requestData.getCurrency());
                message.put("terminalNumber", requestData.getTerminalNumber());
                message.put("transactionDate", paymentResponse.getTransactionDate());
                message.put("referenceId", paymentResponse.getTransactionReference());
                message.put("invoiceNumber", paymentResponse.getInvoiceNumber());

                notifyMerchant(paymentData.getMobile(), message, receiverIdentifier);

                // Update card amount
                PaymentInstrument paymentInstrument = dataSource.getCard(requestData.getSenderCardId());
                paymentInstrument.setBalance(paymentInstrument.getBalance() - paymentResponse.getTotalAmount());

                dataSource.saveCard(paymentInstrument);

                Double tipAmount = null;
                if (paymentData.getTipType() != null) {
                    tipAmount = paymentData.getTipAmount();
                }

                //save transaction
                Transaction transaction = new Transaction();
                transaction.setReferenceId(paymentResponse.getTransactionReference());
                transaction.setCurrencyNumericCode(requestData.getCurrency());
                transaction.setInvoiceNumber(paymentResponse.getInvoiceNumber());
                transaction.setMaskedIdentifier(paymentInstrument.getMaskedIdentifier());
                transaction.setMerchantName(paymentData.getMerchant().getName());
                transaction.setTipAmount(requestData.getTip());
                transaction.setTransactionAmount(requestData.getTransactionAmount());
                transaction.setTransactionDate(paymentResponse.getTransactionDate());

                dataSource.saveTransaction(paymentData.getUserId(), transaction);

                Receipt receipt = new Receipt(paymentData.getMerchant().getName(), paymentData.getMerchant().getCity(), paymentData.getTransactionAmount(), tipAmount, paymentData.getTotal(), paymentData.getCurrencyCode().toString(), paymentInstrument.getMaskedIdentifier(), paymentInstrument.getMethodType());

                paymentView.showReceipt(receipt);
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                paymentView.hideProcessingPaymentLoading();
                paymentRequest = null;

                if (!call.isCanceled()) {
                    paymentView.showNetworkError();
                }
            }
        });
    }

    private void notifyMerchant(final String merchantQRMobile, final Map<String, Object> message, final String receiverIdentifier) {
        boolean isSMS = PreferenceManager.getInstance().getNotificationPreference();
        String storedMobile = PreferenceManager.getInstance().getMobileValue();

        message.put("isSMS", isSMS);

        if (merchantQRMobile != null && isSMS) {
            if (!merchantQRMobile.isEmpty()) {
                final String stringMessage = "You have just received " + paymentData.getTotal() + ".";
                final String mobileNumber = storedMobile.isEmpty() ?
                        merchantQRMobile : storedMobile;

                sendSMSViaTwilio(mobileNumber, stringMessage);
            }
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    NotificationService.getInstance().sendNotification(receiverIdentifier, message);
                }
            });
        }


    }

    private void sendSMSViaTwilio(final String merchantMobileNumber, final String message) {
        okhttp3.Callback callback = new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            }

        };

        RequestBody formBody = new FormBody.Builder()
                .add("To", merchantMobileNumber)
                .add("Body", message)
                .build();
        Request request = new Request.Builder()
                .url("https://serene-temple-92756.herokuapp.com/sms")
                .post(formBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okhttp3.Call response = okHttpClient.newCall(request);
        response.enqueue(callback);
    }


}
