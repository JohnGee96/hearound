package com.hearound.hearound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPost extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Button back = (Button) findViewById(R.id.submitButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String json = getJSONBody();

                // TODO: url needs to be set
                post(url, json);

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }

        });
    }

    String getJSONBody() {
        JSONObject json = new JSONObject();

        EditText userField = (EditText)findViewById(R.id.userID);
        EditText postField = (EditText)findViewById(R.id.postBody);
        EditText latField = (EditText)findViewById(R.id.latitude);
        EditText longField = (EditText)findViewById(R.id.longitude);

        try {
            // TODO: these fields might need to be renamed
            json.put("userID", userField.getText().toString());
            json.put("postBody", postField.getText().toString());
            json.put("latitude", latField.getText().toString());
            json.put("longitude", longField.getText().toString());
        } catch (Exception e) {
            Log.d("****** exception", "empty text box" + e);
        }

        return json.toString();
    }

    // From http://square.github.io/okhttp/
    // Executes a post request
    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
