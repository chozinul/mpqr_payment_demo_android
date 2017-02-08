package com.mastercard.labs.mpqrpayment.receipt;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.activity.MainActivity;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.Receipt;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiptActivity extends AppCompatActivity implements ReceiptContract.View {
    private static String BUNDLE_RECEIPT_KEY = "receipt";

    private ReceiptContract.Presenter presenter;
    private Receipt receipt;

    @BindView(R.id.txt_total_amount)
    TextView totalAmountTextView;

    @BindView(R.id.txt_merchant_name)
    TextView merchantNameTextView;

    @BindView(R.id.txt_merchant_city)
    TextView merchantCityTextView;

    @BindView(R.id.txt_amount_value)
    TextView amountTextView;

    @BindView(R.id.txt_tip)
    TextView tipTitleTextView;

    @BindView(R.id.txt_tip_value)
    TextView tipTextView;

    @BindView(R.id.txt_payment_card_value)
    TextView paymentCardTextView;

    public static Intent newIntent(Context context, Receipt receipt) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_RECEIPT_KEY, receipt);

        Intent intent = new Intent(context, ReceiptActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            receipt = savedInstanceState.getParcelable(BUNDLE_RECEIPT_KEY);
        } else {
            receipt = getIntent().getParcelableExtra(BUNDLE_RECEIPT_KEY);
        }

        presenter = new ReceiptPresenter(this, RealmDataSource.getInstance(), receipt);
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_RECEIPT_KEY, receipt);
    }

    @OnClick(value = R.id.btn_return)
    public void returnBtnPressed() {
        finish();
    }

    @Override
    public void setTotalAmount(Double totalAmount, String currencyCode) {
        totalAmountTextView.setText(String.format(Locale.getDefault(), "%s %,.2f", currencyCode, totalAmount));
    }

    @Override
    public void setMerchantName(String merchantName) {
        merchantNameTextView.setText(merchantName);
    }

    @Override
    public void setMerchantCity(String merchantCity) {
        merchantCityTextView.setText(merchantCity);
    }

    @Override
    public void setAmount(Double amount) {
        amountTextView.setText(String.format(Locale.getDefault(), "%.2f", amount));
    }

    @Override
    public void setTipAmount(Double tipAmount) {
        tipTextView.setText(String.format(Locale.getDefault(), "%.2f", tipAmount));
    }

    @Override
    public void setMaskedPan(String maskedPan) {
        paymentCardTextView.setText(maskedPan);
    }

    @Override
    public void showTipInformation() {
        tipTitleTextView.setVisibility(View.VISIBLE);
        tipTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTipInformation() {
        tipTitleTextView.setVisibility(View.GONE);
        tipTextView.setVisibility(View.GONE);
    }
}
