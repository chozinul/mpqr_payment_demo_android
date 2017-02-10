package com.mastercard.labs.mpqrpayment.network;

import com.mastercard.labs.mpqrpayment.network.mock.MockMPQRPaymentService;
import com.mastercard.labs.mpqrpayment.network.token.TokenInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class ServiceGenerator {
    private static String apiBaseUrl = "http://localhost:8080";
    private static Boolean mockPaymentService = true;
    private static Retrofit retrofit;
    private static MockRetrofit mockRetrofit;

    private static Dispatcher dispatcher;

    private static ServiceGenerator INSTANCE = new ServiceGenerator();

    // No need to instantiate this class.
    private ServiceGenerator() {
        setApiBaseUrl(apiBaseUrl);
    }

    public static ServiceGenerator getInstance() {
        return INSTANCE;
    }

    public void setApiBaseUrl(String newApiBaseUrl) {
        // Cancel all pending requests
        if (dispatcher != null) {
            dispatcher.cancelAll();
        }

        apiBaseUrl = newApiBaseUrl;
        dispatcher = new Dispatcher();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().dispatcher(dispatcher).addInterceptor(loggingInterceptor).addInterceptor(new TokenInterceptor());

        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .baseUrl(apiBaseUrl);

        retrofit = builder.build();

        // Build mock retrofit also
        NetworkBehavior networkBehavior = NetworkBehavior.create();
        networkBehavior.setFailurePercent(0);
        MockRetrofit.Builder mockBuilder = new MockRetrofit.Builder(retrofit)
                .networkBehavior(networkBehavior);

        mockRetrofit = mockBuilder.build();
    }

    public MPQRPaymentService mpqrPaymentService() {
        if (mockPaymentService) {
            return new MockMPQRPaymentService(mockRetrofit.create(MPQRPaymentService.class));
        } else {
            return retrofit.create(MPQRPaymentService.class);
        }
    }
}
