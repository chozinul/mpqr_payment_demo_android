package com.mastercard.labs.mpqrpayment.network;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class ServiceGenerator {
    private static String apiBaseUrl = "http://localhost:8080";
    private static Retrofit retrofit;

    private static Dispatcher dispatcher;

    // No need to instantiate this class.
    private ServiceGenerator() {
        setApiBaseUrl(apiBaseUrl);
    }

    public static void setApiBaseUrl(String newApiBaseUrl) {
        // Cancel all pending requests
        if (dispatcher != null) {
            dispatcher.cancelAll();
        }

        apiBaseUrl = newApiBaseUrl;
        dispatcher = new Dispatcher();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().dispatcher(dispatcher).addInterceptor(loggingInterceptor);

        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .baseUrl(apiBaseUrl);

        retrofit = builder.build();
    }

    public static MPQRPaymentService mpqrPaymentService() {
        return retrofit.create(MPQRPaymentService.class);
    }
}
