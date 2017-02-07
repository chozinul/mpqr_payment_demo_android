package com.mastercard.labs.mpqrpayment.network.mock;

import com.google.gson.Gson;

import com.mastercard.labs.mpqrpayment.network.MPQRPaymentService;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.LoginResponse;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/3/17
 */
public class MockMPQRPaymentService implements MPQRPaymentService {
    private final BehaviorDelegate<MPQRPaymentService> delegate;

    public MockMPQRPaymentService(BehaviorDelegate<MPQRPaymentService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<LoginResponse> login(@Body LoginAccessCodeRequest request) {
        String dummyResponse = "{\n" +
                "  \"id\": 2,\n" +
                "  \"firstName\": \"Muhammad\",\n" +
                "  \"lastName\": \"Azeem\",\n" +
                "  \"paymentInstruments\": [\n" +
                "    {\n" +
                "      \"id\": 4,\n" +
                "      \"acquirerName\": \"Mastercard\",\n" +
                "      \"issuerName\": \"Ecobank\",\n" +
                "      \"name\": \"MastercardGold\",\n" +
                "      \"methodType\": \"DebitCard\",\n" +
                "      \"balance\": \"5100.20\",\n" +
                "      \"maskedIdentifier\": \"**** 0006\",\n" +
                "      \"currencyNumericCode\": 356\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 5,\n" +
                "      \"acquirerName\": \"Mastercard\",\n" +
                "      \"issuerName\": \"Ecobank\",\n" +
                "      \"name\": \"MastercardBlack\",\n" +
                "      \"methodType\": \"CreditCard\",\n" +
                "      \"balance\": \"120.90\",\n" +
                "      \"maskedIdentifier\": \"**** 5101\",\n" +
                "      \"currencyNumericCode\": 356\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 6,\n" +
                "      \"acquirerName\": \"Mastercard\",\n" +
                "      \"issuerName\": \"Ecobank\",\n" +
                "      \"name\": \"MastercardBlack\",\n" +
                "      \"methodType\": \"SavingsAccount\",\n" +
                "      \"balance\": \"21370.00\",\n" +
                "      \"maskedIdentifier\": \"**** 5102\",\n" +
                "      \"currencyNumericCode\": 356\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Gson gson = new Gson();
        LoginResponse response = gson.fromJson(dummyResponse, LoginResponse.class);

        return delegate.returningResponse(response).login(request);
    }

    @Override
    public Call<PaymentResponse> makePayment(@Body PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        response.setApproved(true);
        response.setTransactionDate(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date()));
        response.setTransactionReference(UUID.randomUUID().toString());

        return delegate.returningResponse(response).makePayment(request);
    }
}
