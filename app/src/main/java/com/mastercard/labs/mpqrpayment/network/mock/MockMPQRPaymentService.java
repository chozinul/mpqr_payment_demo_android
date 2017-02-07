package com.mastercard.labs.mpqrpayment.network.mock;

import com.mastercard.labs.mpqrpayment.network.MPQRPaymentService;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
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
    public Call<PaymentResponse> makePayment(@Body PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        response.setApproved(true);
        response.setTransactionDate(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date()));
        response.setTransactionReference(UUID.randomUUID().toString());

        return delegate.returningResponse(response).makePayment(request);
    }
}
