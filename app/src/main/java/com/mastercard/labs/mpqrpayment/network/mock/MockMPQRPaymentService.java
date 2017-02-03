package com.mastercard.labs.mpqrpayment.network.mock;

import com.mastercard.labs.mpqrpayment.network.MPQRPaymentService;
import com.mastercard.labs.mpqrpayment.network.request.QRPaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.QRPaymentResponse;

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
    public Call<QRPaymentResponse> makePayment(@Body QRPaymentRequest request) {
        QRPaymentResponse response = new QRPaymentResponse();
        response.setApproved(true);
        response.setTransactionDate(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date()));
        response.setTransactionReference(UUID.randomUUID().toString());

        return delegate.returningResponse(response).makePayment(request);
    }
}
