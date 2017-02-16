package com.mastercard.labs.mpqrpayment.network.mock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.util.Log;

import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.Merchant;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.network.MPQRPaymentService;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.LoginResponse;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/3/17
 */
public class MockMPQRPaymentService implements MPQRPaymentService {
    private final static String TAG = MockMPQRPaymentService.class.getName();
    private final static int RANDOM_STRING_LENGTH = 8;

    private final String RANDOM_STRING_CHARS = "0123456789ABCDEDGHIJKLMNOPQRSTUVWXYZ";

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final BehaviorDelegate<MPQRPaymentService> delegate;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.getDefault());
    private Gson gson;

    public MockMPQRPaymentService(BehaviorDelegate<MPQRPaymentService> delegate) {
        this.delegate = delegate;
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
    }

    @Override
    public Call<LoginResponse> login(@Body LoginAccessCodeRequest request) {
        if (!request.getAccessCode().equals("12345678") || !request.getPin().equals("123456")) {
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), "{\"success\": \"false\"}");
            return delegate.returning(Calls.response(Response.error(404, responseBody))).login(request);
        }

        String dummyResponse = "{\n" +
                "  \"user\": {\n" +
                "    \"id\": 62,\n" +
                "    \"firstName\": \"Muhammad\",\n" +
                "    \"lastName\": \"Azeem\",\n" +
                "    \"paymentInstruments\": [\n" +
                "      {\n" +
                "        \"id\": 184,\n" +
                "        \"acquirerName\": \"Mastercard\",\n" +
                "        \"issuerName\": \"Ecobank\",\n" +
                "        \"name\": \"MastercardGold\",\n" +
                "        \"methodType\": \"DebitCard\",\n" +
                "        \"balance\": \"5100.20\",\n" +
                "        \"maskedIdentifier\": \"**** 0006\",\n" +
                "        \"currencyNumericCode\": 356,\n" +
                "        \"isDefault\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 185,\n" +
                "        \"acquirerName\": \"Mastercard\",\n" +
                "        \"issuerName\": \"Ecobank\",\n" +
                "        \"name\": \"MastercardBlack\",\n" +
                "        \"methodType\": \"CreditCard\",\n" +
                "        \"balance\": \"120.90\",\n" +
                "        \"maskedIdentifier\": \"**** 5101\",\n" +
                "        \"currencyNumericCode\": 356,\n" +
                "        \"isDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 186,\n" +
                "        \"acquirerName\": \"Mastercard\",\n" +
                "        \"issuerName\": \"Ecobank\",\n" +
                "        \"name\": \"MastercardBlack\",\n" +
                "        \"methodType\": \"SavingsAccount\",\n" +
                "        \"balance\": \"21370.00\",\n" +
                "        \"maskedIdentifier\": \"**** 5102\",\n" +
                "        \"currencyNumericCode\": 356,\n" +
                "        \"isDefault\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NjIsInR5cGUiOiJjb25zdW1lciIsImlhdCI6MTQ4NjUyNTcwOSwiZXhwIjoxNDg3ODIxNzA5fQ.QbRK_RG1yr40iKK2GKmnMoBKuLxLg-X2gsKPnolyJ7w\"\n" +
                "}";

        LoginResponse response = gson.fromJson(dummyResponse, LoginResponse.class);

        return delegate.returningResponse(response).login(request);
    }

    @Override
    public Call<Void> logout() {
        return delegate.returningResponse(null).logout();
    }

    @Override
    public Call<User> consumer() {
        String dummyResponse = "{\n" +
                "  \"id\": 62,\n" +
                "  \"firstName\": \"Muhammad\",\n" +
                "  \"lastName\": \"Azeem\",\n" +
                "  \"paymentInstruments\": [\n" +
                "    {\n" +
                "      \"id\": 184,\n" +
                "      \"acquirerName\": \"Mastercard\",\n" +
                "      \"issuerName\": \"Ecobank\",\n" +
                "      \"name\": \"MastercardGold\",\n" +
                "      \"methodType\": \"DebitCard\",\n" +
                "      \"balance\": \"5100.20\",\n" +
                "      \"maskedIdentifier\": \"**** 0006\",\n" +
                "      \"currencyNumericCode\": 356,\n" +
                "      \"isDefault\": true\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 185,\n" +
                "      \"acquirerName\": \"Mastercard\",\n" +
                "      \"issuerName\": \"Ecobank\",\n" +
                "      \"name\": \"MastercardBlack\",\n" +
                "      \"methodType\": \"CreditCard\",\n" +
                "      \"balance\": \"120.90\",\n" +
                "      \"maskedIdentifier\": \"**** 5101\",\n" +
                "      \"currencyNumericCode\": 356,\n" +
                "      \"isDefault\": false\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 186,\n" +
                "      \"acquirerName\": \"Mastercard\",\n" +
                "      \"issuerName\": \"Ecobank\",\n" +
                "      \"name\": \"MastercardBlack\",\n" +
                "      \"methodType\": \"SavingsAccount\",\n" +
                "      \"balance\": \"21370.00\",\n" +
                "      \"maskedIdentifier\": \"**** 5102\",\n" +
                "      \"currencyNumericCode\": 356,\n" +
                "      \"isDefault\": false\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        User response = gson.fromJson(dummyResponse, User.class);

        return delegate.returningResponse(response).consumer();
    }

    @Override
    public Call<Merchant> merchant(String identifier) {
        if (!identifier.equals("12345678")) {
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), "{\"success\": \"false\"}");
            return delegate.returning(Calls.response(Response.error(404, responseBody))).merchant(identifier);
        }

        String dummyResponse = "{\n" +
                "  \"name\": \"FarmtoTable F&B\",\n" +
                "  \"city\": \"Delhi\",\n" +
                "  \"countryCode\": \"IN\",\n" +
                "  \"categoryCode\": \"1234\",\n" +
                "  \"currencyNumericCode\": \"356\",\n" +
                "  \"storeId\": \"87654321\",\n" +
                "  \"terminalNumber\": \"3124652125\",\n" +
                "  \"identifierMastercard04\": \"5555222233334444\",\n" +
                "}";

        Merchant response = gson.fromJson(dummyResponse, Merchant.class);

        return delegate.returningResponse(response).merchant(identifier);
    }

    @Override
    public Call<PaymentResponse> makePayment(@Body final PaymentRequest request) {
        PaymentInstrument instrument = RealmDataSource.getInstance().getCard(request.getSenderCardId());

        final PaymentResponse response;
        if (instrument == null) {
            response = new PaymentResponse(false, null, null, null, "invalid_card", 0);
        } else if (instrument.getBalance() < request.getTotal()) {
            response = new PaymentResponse(false, null, null, null, "insufficient_balance", 0);
        } else {
            response = new PaymentResponse(true, RandomStringUtils.random(RANDOM_STRING_LENGTH, RANDOM_STRING_CHARS), new Date(), RandomStringUtils.random(RANDOM_STRING_LENGTH, RANDOM_STRING_CHARS), null, request.getTotal());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    sendNotification(request, response);
                }
            });
        }

        return delegate.returningResponse(response).makePayment(request);
    }

    private void sendNotification(PaymentRequest request, PaymentResponse response) {
        final String API_KEY = "AAAAWVlC7eo:APA91bFAAT4qSLOaL2J2wYj5RjynW9-tRSf2HLkdd6wpeQE5qNwJzA3v2kk0vZGXZPU0lsvLIivawCUJ1kG8oUnRJ8jvYihg7IjSn-nd8JhQciJe9ImfaBaCJRdopyJCj7kECJxo9zGw";
        final String merchantCode = request.getReceiverCardNumber();

        GCMMessage message = new GCMMessage(request.getTransactionAmount(), request.getTip(), request.getCurrency(), response.getTransactionDate(), response.getTransactionReference(), request.getTerminalNumber(), response.getInvoiceNumber());

        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            Map<String, Object> data = new HashMap<>();
            data.put("to", "/topics/" + merchantCode);
            data.put("data", message);

            // Create connection to send GCM Message request.
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(gson.toJson(data).getBytes());

            // Read GCM response.
            String resp = conn.getResponseMessage();
            Log.d(TAG, resp);
            Log.d(TAG, "Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (IOException e) {
            Log.d(TAG, "Unable to send GCM message.");
            Log.d(TAG, "Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
    }

    private class GCMMessage {
        private double transactionAmount;
        private double tipAmount;
        private String currencyNumericCode;
        private Date transactionDate;
        private String referenceId;
        private String terminalNumber;
        private String invoiceNumber;

        GCMMessage(double transactionAmount, double tip, String currencyNumericCode, Date transactionDate, String referenceId, String terminalNumber, String invoiceNumber) {
            this.transactionAmount = transactionAmount;
            this.tipAmount = tip;
            this.currencyNumericCode = currencyNumericCode;
            this.transactionDate = transactionDate;
            this.referenceId = referenceId;
            this.terminalNumber = terminalNumber;
            this.invoiceNumber = invoiceNumber;
        }
    }
}
