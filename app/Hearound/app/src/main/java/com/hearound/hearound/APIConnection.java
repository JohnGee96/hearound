package com.hearound.hearound;

import android.util.Log;

import java.io.IOException;
import java.io.StreamCorruptedException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Maxwell on 10/29/17.
 */

public class APIConnection {
    private final OkHttpClient client = new OkHttpClient();


    public String post(String url) throws Exception {
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(callback);
    }
}
