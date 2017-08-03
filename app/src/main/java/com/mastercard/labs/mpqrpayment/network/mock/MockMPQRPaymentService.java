package com.mastercard.labs.mpqrpayment.network.mock;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mastercard.labs.mpqrpayment.MainApplication;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.Transaction;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.network.LoginManager;
import com.mastercard.labs.mpqrpayment.network.MPQRPaymentService;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.LoginResponse;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;
import com.mastercard.labs.mpqrpayment.utils.PreferenceManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import io.realm.RealmList;
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

    private final static String RANDOM_STRING_CHARS = "0123456789ABCDEDGHIJKLMNOPQRSTUVWXYZ";
    private static final String TRANSACTIONS_LIST_KEY = "merchantTransactions";

    private static final String NUMBER_OF_CARDS_KEY = "numberOfCards";

    private static int DEFAULT_NUMBER_OF_CARDS;

    static {
        try {
            AssetManager assetManager = MainApplication.getInstance().getBaseContext().getAssets();
            Properties properties = new Properties();
            properties.load(assetManager.open("init.properties"));
            String numberString = properties.getProperty(NUMBER_OF_CARDS_KEY, "3");
            DEFAULT_NUMBER_OF_CARDS = Integer.parseInt(numberString);
            if (DEFAULT_NUMBER_OF_CARDS > 3 || DEFAULT_NUMBER_OF_CARDS < 1) {
                DEFAULT_NUMBER_OF_CARDS = 3;
            }
        } catch (Exception e) {
            //swallow it
            DEFAULT_NUMBER_OF_CARDS = 3;
        }
    }

    private final BehaviorDelegate<MPQRPaymentService> delegate;

    private Gson gson;

    public MockMPQRPaymentService(BehaviorDelegate<MPQRPaymentService> delegate) {
        this.delegate = delegate;
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
    }

    @Override
    public Call<LoginResponse> login(@Body LoginAccessCodeRequest request) {
        if (request.getAccessCode().length() == 0 || !request.getPin().equals("123456")) {
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), "{\"success\": \"false\"}");
            return delegate.returning(Calls.response(Response.error(404, responseBody))).login(request);
        }

        // TODO: Handle version updates because that might invalidate stored data in preferences and cause exceptions while parsing as JSON
        // Parse stored transactions
        Set<String> transactions = PreferenceManager.getInstance().getStringSet(TRANSACTIONS_LIST_KEY, new HashSet<String>());
        List<Transaction> transactionList = new ArrayList<>(transactions.size());
        for (String transaction : transactions) {
            transactionList.add(gson.fromJson(transaction, Transaction.class));
        }

        String dummyResponse = "{\n" +
                "  \"user\": " + USER_JSON + "\n," +
                "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NjIsInR5cGUiOiJjb25zdW1lciIsImlhdCI6MTQ4NjUyNTcwOSwiZXhwIjoxNDg3ODIxNzA5fQ.QbRK_RG1yr40iKK2GKmnMoBKuLxLg-X2gsKPnolyJ7w\"\n" +
                "}";

        LoginResponse response = gson.fromJson(dummyResponse, LoginResponse.class);
        response.getUser().setTransactions(new RealmList<>(transactionList.toArray(new Transaction[]{})));

        PaymentInstrument[] instruments = response.getUser().getPaymentInstruments().toArray(new PaymentInstrument[0]);
        RealmList<PaymentInstrument> shortListedInstruments = new RealmList<>(ArrayUtils.subarray(instruments, 0, DEFAULT_NUMBER_OF_CARDS));
        response.getUser().setPaymentInstruments(shortListedInstruments);

        return delegate.returningResponse(response).login(request);
    }

    @Override
    public Call<Void> logout() {
        List<Transaction> transactions = RealmDataSource.getInstance().getAllTransactions(LoginManager.getInstance().getLoggedInUserId());
        if (transactions != null) {
            Set<String> jsonTransactions = new HashSet<>(transactions.size());
            for (Transaction transaction : transactions) {
                try {
                    jsonTransactions.add(gson.toJson(transaction));
                } catch (Exception ex) {
                    // Ignore exception
                    ex.printStackTrace();
                }
            }

            PreferenceManager.getInstance().putStringSet(TRANSACTIONS_LIST_KEY, jsonTransactions);
        }

        return delegate.returningResponse(null).logout();
    }

    @Override
    public Call<User> consumer() {
        String dummyResponse = USER_JSON;

        User response = gson.fromJson(dummyResponse, User.class);

        return delegate.returningResponse(response).consumer();
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
        }

        return delegate.returningResponse(response).makePayment(request);
    }

    private final static String USER_JSON = "{\n" +
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
            "      \"balance\": \"2500.90\",\n" +
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
            "      \"balance\": \"2800.00\",\n" +
            "      \"maskedIdentifier\": \"**** 5102\",\n" +
            "      \"currencyNumericCode\": 356,\n" +
            "      \"isDefault\": false\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
