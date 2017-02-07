package com.mastercard.labs.mpqrpayment.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.activity.MainActivity;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * A login screen that offers login via access code & pin.
 */
public class LoginActivity extends AppCompatActivity implements LoginContract.View {
    private LoginContract.Presenter presenter;

    // UI references.
    @BindView(R.id.txt_access_code)
    EditText mAccessCodeEditText;

    @BindView(R.id.txt_pin)
    EditText mPinEditText;

    @BindView(R.id.login_progress)
    View mProgressView;

    @BindView(R.id.login_form)
    View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mPinEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    presenter.login(mAccessCodeEditText.getText().toString(), mPinEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        presenter = new LoginPresenter(this, RealmDataSource.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.start();
    }

    @OnClick(value = R.id.email_sign_in_button)
    public void signInButtonPressed() {
        presenter.login(mAccessCodeEditText.getText().toString(), mPinEditText.getText().toString());
    }

    @OnFocusChange(value = {R.id.txt_access_code, R.id.txt_pin})
    public void focusChanged(View view, boolean hasFocus) {
        if (!hasFocus) {
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    @Override
    public void clearAccessCodeError() {
        mAccessCodeEditText.setError(null);
        mAccessCodeEditText.clearFocus();
    }

    @Override
    public void clearPinError() {
        mPinEditText.setError(null);
        mPinEditText.clearFocus();
    }

    @Override
    public void setAccessCodeRequired() {
        mAccessCodeEditText.setError(getString(R.string.error_field_required));
        mAccessCodeEditText.requestFocus();
    }

    @Override
    public void showProgress() {
        showProgress(true);
    }

    @Override
    public void hideProgress() {
        showProgress(false);
    }

    @Override
    public void startMainFlow() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void setIncorrectPin() {
        mPinEditText.setError(getString(R.string.error_incorrect_pin));
        mPinEditText.requestFocus();
    }

    @Override
    public void setInvalidPinError() {
        mPinEditText.setError(getString(R.string.error_incorrect_pin));
        mPinEditText.requestFocus();
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
    }
}

