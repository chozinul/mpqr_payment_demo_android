package com.mastercard.labs.mpqrpayment.network.mock;

import com.google.gson.Gson;

import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.Merchant;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.network.MPQRPaymentService;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.LoginResponse;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
    private final BehaviorDelegate<MPQRPaymentService> delegate;

    public MockMPQRPaymentService(BehaviorDelegate<MPQRPaymentService> delegate) {
        this.delegate = delegate;
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

        Gson gson = new Gson();
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

        Gson gson = new Gson();
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
                "  \"code\": \"12345678\",\n" +
                "  \"name\": \"FarmtoTable F&B\",\n" +
                "  \"city\": \"Delhi\",\n" +
                "  \"categoryCode\": \"123\",\n" +
                "  \"identifierMastercard04\": \"5555222233334444\"\n" +
                "}";

        Gson gson = new Gson();
        Merchant response = gson.fromJson(dummyResponse, Merchant.class);

        return delegate.returningResponse(response).merchant(identifier);
    }

    @Override
    public Call<PaymentResponse> makePayment(@Body PaymentRequest request) {
        PaymentInstrument instrument = RealmDataSource.getInstance().getCard(request.getSenderCardId());

        PaymentResponse response;
        if (instrument == null) {
            response = new PaymentResponse(false, null, null, "invalid_card", 0);
        } else if (instrument.getBalance() < request.getTransactionAmount()) {
            response = new PaymentResponse(false, null, null, "insufficient_balance", 0);
        } else {
            String transactionDate = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
            response = new PaymentResponse(true, UUID.randomUUID().toString(), transactionDate, null, request.getTransactionAmount());
        }

        return delegate.returningResponse(response).makePayment(request);
    }
}
