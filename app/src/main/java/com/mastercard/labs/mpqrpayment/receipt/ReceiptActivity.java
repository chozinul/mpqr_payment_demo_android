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
    private static String BUNDLE_MERCHANT_NAME = "merchantName";
    private static String BUNDLE_MERCHANT_CITY = "merchantCity";
    private static String BUNDLE_AMOUNT = "amount";
    private static String BUNDLE_TIP_AMOUNT = "tipAmount";
    private static String BUNDLE_TOTAL_AMOUNT = "totalAmount";
    private static String BUNDLE_CURRENCY_CODE = "currencyCode";
    private static String BUNDLE_MASKED_PAN = "maskedPan";

    private ReceiptContract.Presenter presenter;

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
        bundle.putString(BUNDLE_MERCHANT_NAME, receipt.getMerchantName());
        bundle.putString(BUNDLE_MERCHANT_CITY, receipt.getMerchantCity());
        bundle.putDouble(BUNDLE_AMOUNT, receipt.getAmount());
        bundle.putDouble(BUNDLE_TIP_AMOUNT, receipt.getTipAmount());
        bundle.putDouble(BUNDLE_TOTAL_AMOUNT, receipt.getTotalAmount());
        bundle.putString(BUNDLE_CURRENCY_CODE, receipt.getCurrencyCode());
        bundle.putString(BUNDLE_MASKED_PAN, receipt.getMaskedPan());

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

        Bundle bundle = getIntent().getExtras();
        Receipt receipt = new Receipt(bundle.getString(BUNDLE_MERCHANT_NAME),
                bundle.getString(BUNDLE_MERCHANT_CITY),
                bundle.getDouble(BUNDLE_AMOUNT),
                bundle.getDouble(BUNDLE_TIP_AMOUNT),
                bundle.getDouble(BUNDLE_TOTAL_AMOUNT),
                bundle.getString(BUNDLE_CURRENCY_CODE),
                bundle.getString(BUNDLE_MASKED_PAN));

        presenter = new ReceiptPresenter(this, RealmDataSource.getInstance(), receipt);
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.start();
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
