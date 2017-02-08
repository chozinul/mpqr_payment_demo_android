package com.mastercard.labs.mpqrpayment.network;

import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.LoginResponse;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public interface MPQRPaymentService {
    @POST("/consumer/login")
    Call<LoginResponse> login(@Body LoginAccessCodeRequest request);

    @GET("/consumer")
    Call<User> consumer();

    @POST("/qr-pay")
    Call<PaymentResponse> makePayment(@Body PaymentRequest request);
}
