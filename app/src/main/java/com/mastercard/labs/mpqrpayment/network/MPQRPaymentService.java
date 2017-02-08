package com.mastercard.labs.mpqrpayment.network;

import com.mastercard.labs.mpqrpayment.data.model.Merchant;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;
import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.LoginResponse;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public interface MPQRPaymentService {
    @POST("/consumer/login")
    Call<LoginResponse> login(@Body LoginAccessCodeRequest request);

    @POST("/consumer/logout")
    Call<Void> logout();

    @GET("/consumer")
    Call<User> consumer();

    @GET("/merchant/{id}")
    Call<Merchant> merchant(@Path("id") String identifier);

    @POST("/qr-pay")
    Call<PaymentResponse> makePayment(@Body PaymentRequest request);
}
