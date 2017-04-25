package com.mastercard.labs.mpqrpayment.network.mock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.network.MPQRPaymentService;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.LoginResponse;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

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

        String dummyResponse = "{\n" +
                "  \"user\": " + USER_JSON + "\n," +
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
