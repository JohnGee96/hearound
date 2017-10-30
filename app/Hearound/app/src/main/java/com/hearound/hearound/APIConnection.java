package com.hearound.hearound;

import android.util.Log;

import java.io.IOException;
import java.io.StreamCorruptedException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Maxwell on 10/29/17.
 */

public class APIConnection {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public void post(String url, String json, Callback callback) throws Exception {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(callback);
    }

    public void get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(callback);
    }
}
