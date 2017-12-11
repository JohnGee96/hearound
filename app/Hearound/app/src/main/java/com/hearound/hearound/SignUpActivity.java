package com.hearound.hearound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    private String API_URL;
    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        API_URL = getString(R.string.api_url);

        usernameView = (EditText) findViewById(R.id.username);
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        confirmPasswordView = (EditText) findViewById(R.id.confirm_password);

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: verify unique username, verify email (unique), verify passwords match, and post to api
                attemptSignUp();
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptSignUp() {
        // Reset errors.
        usernameView.setError(null);
        emailView.setError(null);
        passwordView.setError(null);
        confirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameView.getText().toString();
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            passwordView.setError(getString(R.string.error_passwords_dont_match));
            confirmPasswordView.setError(getString(R.string.error_passwords_dont_match));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            createUser(email, username, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void createUser(String email, String username, String password) {
        APIConnection api = new APIConnection();

        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("username", username);
            params.put("password", password);

            api.post(API_URL + "/signup", params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    final String error = e.toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createAlert(getString(R.string.server_error));
                            Log.e("**** post ****", error);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() == 200) {
                        setUserInfo(response.body().string());
                    } else {
                        createAlert(getString(R.string.error_signup_failed));
                    }
                }
            });
        } catch (IOException e1) {
            Log.e("**** createUser ***", "Error with POST: " + e1);
        } catch (JSONException e2) {
            Log.e("**** createUser ***", "Error making JSON" + e2);
        } catch (Exception e) {
            Log.e("**** createUser ***", e.toString());
        }
    }

    private void createAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void setUserInfo(String data) {
        try {
            JSONObject userData = new JSONObject(data);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", userData.getString("username"));
            editor.putString("email", userData.getString("email"));
            editor.putInt("user_id", userData.getInt("user_id"));
            editor.apply();

        } catch (Exception e) {
            Log.e("**** setUserInfo ****", "error parsing response data: " + e);
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
