package com.mastercard.labs.mpqrpayment.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.util.Log;

import com.mastercard.labs.mpqrpayment.network.request.PaymentRequest;
import com.mastercard.labs.mpqrpayment.network.response.PaymentResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/22/17
 */
public class NotificationService {
    private final static String TAG = NotificationService.class.getName();

    private final Gson gson;

    private static final NotificationService INSTANCE = new NotificationService();

    private NotificationService() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
    }

    public static NotificationService getInstance() {
        return INSTANCE;
    }

    public void sendNotification(String identifier, Map<String, Object> message) {
        final String API_KEY = "AAAAWVlC7eo:APA91bFAAT4qSLOaL2J2wYj5RjynW9-tRSf2HLkdd6wpeQE5qNwJzA3v2kk0vZGXZPU0lsvLIivawCUJ1kG8oUnRJ8jvYihg7IjSn-nd8JhQciJe9ImfaBaCJRdopyJCj7kECJxo9zGw";

        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            Map<String, Object> data = new HashMap<>();
            data.put("to", "/topics/" + identifier);
            data.put("data", message);

            // Create connection to send GCM Message request.
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(gson.toJson(data).getBytes());

            // Read GCM response.
            String resp = conn.getResponseMessage();
            Log.d(TAG, resp);
            Log.d(TAG, "Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (IOException e) {
            Log.d(TAG, "Unable to send GCM message.");
            Log.d(TAG, "Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
    }
}
