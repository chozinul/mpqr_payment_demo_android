package com.mastercard.labs.mpqrpayment.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.network.ServiceGenerator;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via access code & pin.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mAccessCodeEditText;
    private EditText mPinEditText;
    private View mProgressView;
    private View mLoginFormView;

    private Call<User> authRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mAccessCodeEditText = (EditText) findViewById(R.id.accessCode);

        mPinEditText = (EditText) findViewById(R.id.pin);
        mPinEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
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

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form. If there are form
     * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
     * attempt is made.
     */
    private void attemptLogin() {
        if (authRequest != null) {
            return;
        }

        // Reset errors.
        mAccessCodeEditText.setError(null);
        mPinEditText.setError(null);

        // Store values at the time of the login attempt.
        String accessCode = mAccessCodeEditText.getText().toString();
        String pin = mPinEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid pin, if the user entered one.
        if (!TextUtils.isEmpty(pin) && !isPinValid(pin)) {
            mPinEditText.setError(getString(R.string.error_invalid_pin));
            focusView = mPinEditText;
            cancel = true;
        }

        // Check for a valid access codes.
        if (TextUtils.isEmpty(accessCode)) {
            mAccessCodeEditText.setError(getString(R.string.error_field_required));
            focusView = mAccessCodeEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            authRequest = ServiceGenerator.getInstance().mpqrPaymentService().login(new LoginAccessCodeRequest(accessCode, pin));
            authRequest.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    showProgress(false);

                    finish();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showProgress(false);

                    if (!call.isCanceled()) {
                        mPinEditText.setError(getString(R.string.error_incorrect_pin));
                        mPinEditText.requestFocus();
                    }
                }
            });
        }
    }

    private boolean isPinValid(String pin) {
        return pin.length() == 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

