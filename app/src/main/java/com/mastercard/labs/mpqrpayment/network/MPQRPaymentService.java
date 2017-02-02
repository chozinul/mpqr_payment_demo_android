package com.mastercard.labs.mpqrpayment.network;

import com.mastercard.labs.mpqrpayment.network.request.QRPaymentRequest;

import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public interface MPQRPaymentService {
    @POST("/qr-pay")
    public void makePayment(@Body QRPaymentRequest request);
}
