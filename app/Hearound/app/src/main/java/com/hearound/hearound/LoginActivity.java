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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private String API_URL;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        API_URL = getString(R.string.api_url);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            loginUser(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void loginUser(String email, String password) {
        APIConnection api = new APIConnection();

        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);

            api.post(API_URL + "/login", params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    final String error = e.toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("**** post ****", error);
                            createAlert(getString(R.string.server_error));
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() == 200) {
                        setUserInfo(response.body().string());
                    } else {
                        createAlert(getString(R.string.error_invalid_login));
                    }
                }
            });
        } catch (IOException e1) {
            Log.e("**** loginUser ***", "Error with POST: " + e1);
        } catch (JSONException e2) {
            Log.e("**** loginUser ***", "Error making JSON" + e2);
        } catch (Exception e) {
            Log.e("**** loginUser ***", e.toString());
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

