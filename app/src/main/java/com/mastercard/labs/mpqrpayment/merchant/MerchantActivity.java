package com.mastercard.labs.mpqrpayment.merchant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;
import com.mastercard.labs.mpqrpayment.payment.PaymentActivity;
import com.mastercard.labs.mpqrpayment.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MerchantActivity extends AppCompatActivity implements MerchantContract.View {
    private static final String BUNDLE_USER_ID_KEY = "userId";
    private static final String BUNDLE_SELECTED_CARD_ID_KEY = "cardId";
    private static final String BUNDLE_CURRENCY_NUMERIC_CODE_KEY = "currencyNumericCode";

    MerchantContract.Presenter presenter;
    long userId;
    long selectedCardId;
    String currencyNumericCode;

    @BindView(R.id.txt_merchant_code_value)
    EditText merchantCodeEdiText;

    @BindView(R.id.pb_merchant_info)
    ProgressBar merchantInfoProgressBar;

    @BindView(R.id.ll_merchant_detail)
    LinearLayout merchantDetailLayout;

    @BindView(R.id.txt_merchant_name_value)
    EditText merchantNameEditText;

    @BindView(R.id.txt_merchant_city_value)
    EditText merchantCityEditText;

    public static Intent newIntent(Context context, long userId, long selectedCardId, String currencyNumericCode) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_USER_ID_KEY, userId);
        bundle.putLong(BUNDLE_SELECTED_CARD_ID_KEY, selectedCardId);
        bundle.putString(BUNDLE_CURRENCY_NUMERIC_CODE_KEY, currencyNumericCode);

        Intent intent = new Intent(context, MerchantActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            userId = savedInstanceState.getLong(BUNDLE_USER_ID_KEY, -1);
            selectedCardId = savedInstanceState.getLong(BUNDLE_SELECTED_CARD_ID_KEY, -1);
            currencyNumericCode = savedInstanceState.getString(BUNDLE_CURRENCY_NUMERIC_CODE_KEY);
        } else {
            userId = getIntent().getLongExtra(BUNDLE_USER_ID_KEY, -1);
            selectedCardId = getIntent().getLongExtra(BUNDLE_SELECTED_CARD_ID_KEY, -1);
            currencyNumericCode = getIntent().getStringExtra(BUNDLE_CURRENCY_NUMERIC_CODE_KEY);
        }

        presenter = new MerchantPresenter(userId, selectedCardId, currencyNumericCode, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(BUNDLE_USER_ID_KEY, userId);
        outState.putLong(BUNDLE_SELECTED_CARD_ID_KEY, selectedCardId);
        outState.putString(BUNDLE_CURRENCY_NUMERIC_CODE_KEY, currencyNumericCode);
    }

    @OnClick(value = R.id.next_button)
    public void nextButtonPressed() {
        presenter.moveToNextStep();
    }

    @OnTextChanged(value = R.id.txt_merchant_code_value, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onMerchantCodeChanged(Editable text) {
        presenter.updateMerchantCode(text.toString());
    }

    @Override
    public void showInvalidMerchantError() {
        merchantCodeEdiText.setError(getString(R.string.invalid_merchant_code));
    }

    @Override
    public void clearCode() {
        hideInfoProgress();
        hideMerchantInfo();

        merchantCodeEdiText.setText(null);
        merchantCodeEdiText.setError(null);
        merchantCodeEdiText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    public void hideMerchantInfo() {
        merchantDetailLayout.setVisibility(View.GONE);
        merchantCodeEdiText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        merchantNameEditText.setText(null);
        merchantCityEditText.setText(null);
    }

    @Override
    public void showMerchantInfo(String name, String city) {
        hideInfoProgress();

        merchantDetailLayout.setVisibility(View.VISIBLE);
        merchantCodeEdiText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.success_tick, 0);

        merchantNameEditText.setText(name);

        merchantCityEditText.setText(city);
    }

    @Override
    public void showInfoProgress() {
        merchantCodeEdiText.setEnabled(false);
        merchantCodeEdiText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        merchantInfoProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideInfoProgress() {
        merchantCodeEdiText.setEnabled(true);
        merchantInfoProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNetworkError() {
        DialogUtils.showErrorDialog(this, R.string.error, R.string.unexpected_error);
    }

    @Override
    public void showPaymentActivity(PaymentData paymentData) {
        Intent intent = PaymentActivity.newIntent(this, paymentData);

        startActivity(intent);

        finish();
    }
}
