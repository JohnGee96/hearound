package com.hearound.hearound;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPost extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    // TODO: set url
    private String API_URL;
    private EditText titleView;
    private EditText bodyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        API_URL = getString(R.string.api_url);
        titleView = (EditText) findViewById(R.id.title);
        bodyView = (EditText) findViewById(R.id.postBody);

        Button submit = (Button) findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!fieldsValid()) {
                    return;
                }
                JSONObject json = getJSONBody();
                if (json.length() == 0) {
                    return;
                }

                try {
                    APIConnection api = new APIConnection();
                    api.post(API_URL + "/posts", json, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            final String error = e.toString();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("**** post ****", error);
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // Don't need to do anything with the response at this point
                            return;
                        }
                    });
                } catch (Exception e) {
                    Log.e("**** onClick ****", "error with POST: " + e);
                }

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }

        });

        Button cancel = (Button) findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean fieldsValid() {
        // Reset errors.
        titleView.setError(null);
        bodyView.setError(null);

        String title = titleView.getText().toString();
        String body = bodyView.getText().toString();

        boolean valid = true;
        View focusView = null;

        if (TextUtils.isEmpty(body)) {
            bodyView.setError(getString(R.string.error_field_required));
            focusView = bodyView;
            valid = false;
        }

        if (TextUtils.isEmpty(title)) {
            titleView.setError(getString(R.string.error_field_required));
            focusView = titleView;
            valid = false;
        }

        if (!valid) {
            focusView.requestFocus();
        }

        return valid;
    }

    private JSONObject getJSONBody() {
        JSONObject json = new JSONObject();

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);

        try {
            json.put("title", titleView.getText().toString());
            json.put("body", bodyView.getText().toString());
            json.put("lat", lat);
            json.put("lng", lng);
            Log.d("**** getJSONBody ****", lat + "");
            Log.d("**** getJSONBody ****", lng + "");
        } catch (Exception e) {
            // TODO: add user dialogue
            Log.e("**** getJSONBody ****", "empty text box: " + e);
        }

        return json;
    }
}
