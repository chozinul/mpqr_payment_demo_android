package com.mastercard.labs.mpqrpayment.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.CardsArrayAdapter;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.MethodType;
import com.mastercard.labs.mpqrpayment.data.model.Receipt;
import com.mastercard.labs.mpqrpayment.receipt.ReceiptActivity;
import com.mastercard.labs.mpqrpayment.utils.DialogUtils;
import com.mastercard.labs.mpqrpayment.utils.DrawableUtils;
import com.mastercard.labs.mpqrpayment.view.SuffixEditText;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PaymentActivity extends AppCompatActivity implements PaymentContract.View {
    public static String BUNDLE_PAYMENT_DATA_KEY = "paymentData";

    private PaymentContract.Presenter presenter;

    @BindView(R.id.txt_currency_value)
    TextView currencyTextView;

    @BindView(R.id.txt_amount_value)
    EditText amountEditText;

    @BindView(R.id.txt_tip)
    TextView tipTitleTextView;

    @BindView(R.id.txt_tip_value)
    SuffixEditText tipEditText;

    @BindView(R.id.txt_total_amount)
    TextView totalAmountTextView;

    @BindView(R.id.txt_merchant_name)
    TextView merchantNameTextView;

    @BindView(R.id.txt_merchant_city)
    TextView merchantCityTextView;

    @BindView(R.id.txt_payment_card)
    TextView paymentCardTextView;

    @BindView(R.id.rl_tip)
    RelativeLayout tipLayout;

    @BindView(R.id.top_border_tip)
    View topBorderTip;

    private PaymentData paymentData;

    private boolean blockAmountTextViewChange;
    private boolean blockTipTextViewChange;

    private ProgressDialog progressDialog;

    private AmountInputFilter tipInputFilter = new AmountInputFilter(0, Double.MAX_VALUE);

    public static Intent newIntent(Context context, PaymentData paymentData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PaymentActivity.BUNDLE_PAYMENT_DATA_KEY, paymentData);

        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            paymentData = savedInstanceState.getParcelable(BUNDLE_PAYMENT_DATA_KEY);
        } else {
            paymentData = getIntent().getParcelableExtra(BUNDLE_PAYMENT_DATA_KEY);
        }

        presenter = new PaymentPresenter(this, RealmDataSource.getInstance(), paymentData);

        amountEditText.setFilters(new InputFilter[]{new AmountInputFilter(0, Double.MAX_VALUE)});
        tipEditText.setFilters(new InputFilter[]{tipInputFilter});
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_PAYMENT_DATA_KEY, paymentData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setPaymentData(paymentData);
        presenter.start();
    }

    @OnTextChanged(value = R.id.txt_amount_value, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAmountChanged(Editable amountText) {
        if (blockAmountTextViewChange) {
            return;
        }

        blockAmountTextViewChange = true;

        double amount = parseAmount(amountText.toString());
        updateEditText(amountEditText, amount);

        blockAmountTextViewChange = false;

        presenter.setAmount(amount);
    }

    @OnTextChanged(value = R.id.txt_tip_value, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTipChanged(Editable tipText) {
        if (blockTipTextViewChange) {
            return;
        }

        blockTipTextViewChange = true;

        double tip = parseAmount(tipText.toString());
        updateEditText(tipEditText, tip);

        blockTipTextViewChange = false;

        presenter.setTip(tip);
    }

    @OnClick(value = R.id.rl_payment_card)
    public void changeCardBtnPressed() {
        presenter.selectCard();
    }

    @OnClick(value = R.id.btn_pay)
    public void payBtnPressed() {
        presenter.makePayment();
    }

    private double parseAmount(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            int count = amountText.length() - amountText.indexOf(".") - 1;
            amount *= Math.pow(10, count);

            return amount / 100;
        } catch (Exception ex) {
            return 0;
        }
    }

    private void updateEditText(EditText editText, double amount) {
        editText.getText().clear();

        String text = String.format(Locale.getDefault(), "%.2f", amount);
        editText.getText().append(text);

        if (amount == 0) {
            editText.setTextColor(ContextCompat.getColor(this, R.color.colorLightGrey));
        } else {
            editText.setTextColor(ContextCompat.getColor(this, R.color.colorTextMainColor));
        }
    }

    @Override
    public void setCurrency(String currency) {
        currencyTextView.setText(currency);
    }

    @Override
    public void setAmount(double amount) {
        blockAmountTextViewChange = true;
        updateEditText(amountEditText, amount);
        blockAmountTextViewChange = false;
    }

    private void setTipHasPercentage(boolean hasPercentage) {
        tipEditText.setSuffix(hasPercentage ? " %" : "");
        tipInputFilter.setMax(hasPercentage ? 100 : Double.MAX_VALUE);
    }

    @Override
    public void setFlatConvenienceFee(double fee) {
        tipTitleTextView.setText(R.string.flat_convenience_fee);
        setTipHasPercentage(false);
        setTip(fee);
    }

    @Override
    public void setPercentageConvenienceFee(double feePercentage) {
        tipTitleTextView.setText(R.string.percentage_convenience_fee);
        setTipHasPercentage(true);
        setTip(feePercentage);
    }

    @Override
    public void setPromptToEnterTip(double tip) {
        tipTitleTextView.setText(R.string.tip);
        setTipHasPercentage(false);
        setTip(tip);
    }

    private void setTip(double amount) {
        blockTipTextViewChange = true;
        updateEditText(tipEditText, amount);
        blockTipTextViewChange = false;
    }

    @Override
    public void disableTipChange() {
        tipLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDisabledDeepSeaBlue));
        tipTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.colorDeepSeaBlue));
        tipEditText.setEnabled(false);
    }

    @Override
    public void enableTipChange() {
        tipLayout.setBackgroundColor(Color.TRANSPARENT);
        tipTitleTextView.setTextColor(ContextCompat.getColor(this, R.color.colorWarmGrey));
        tipEditText.setEnabled(true);
    }

    @Override
    public void setTotalAmount(double amount, String currencyCode) {
        totalAmountTextView.setText(String.format(Locale.getDefault(), "%s %,.2f", currencyCode, amount));

        if (amount == 0) {
            totalAmountTextView.setTextColor(ContextCompat.getColor(this, R.color.colorLightGrey));
        } else {
            totalAmountTextView.setTextColor(ContextCompat.getColor(this, R.color.colorTextMainColor));
        }
    }

    @Override
    public void hideTipInformation() {
        topBorderTip.setVisibility(View.GONE);
        tipLayout.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(currencyTextView.getLayoutParams());
        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.addRule(RelativeLayout.BELOW, R.id.txt_currency);
        currencyTextView.setLayoutParams(layoutParams);
        currencyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
    }

    @Override
    public void showTipInformation() {
        topBorderTip.setVisibility(View.VISIBLE);
        tipLayout.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(currencyTextView.getLayoutParams());
        layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.addRule(RelativeLayout.BELOW, R.id.txt_currency);
        currencyTextView.setLayoutParams(layoutParams);
        currencyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_expand_more);
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
    public void setCard(PaymentInstrument paymentInstrument) {
        @DrawableRes int imageId = DrawableUtils.getPaymentMethodLogo(paymentInstrument);

        paymentCardTextView.setCompoundDrawablesWithIntrinsicBounds(imageId, 0, 0, 0);
        paymentCardTextView.setText(getString(R.string.pay_with_card, paymentInstrument.getMaskedIdentifier()));
    }

    @Override
    public void showCardSelection(final List<PaymentInstrument> paymentInstruments, int selectedCardIdx) {
        final CardsArrayAdapter adapter = new CardsArrayAdapter(this, paymentInstruments);
        adapter.setSelectedIndex(selectedCardIdx);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(adapter, selectedCardIdx, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.setSelectedIndex(which);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setCancelable(true)
                .setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.setCardId(paymentInstruments.get(adapter.getSelectedIndex()).getId());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.show();
    }

    @Override
    public void askPin(int pinLength) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(pinLength)});
        int margin = getResources().getDimensionPixelSize(R.dimen.size_10);
        input.setPadding(margin, margin, margin, margin);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.enter_pin_title)
                .setMessage(R.string.enter_pin_message)
                .setView(input)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        presenter.pin(input.getText().toString());
                    }
                }).create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();
    }

    @Override
    public void showProcessingPaymentLoading() {
        hideProcessingPaymentLoading();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.processing_payment_message));
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    @Override
    public void hideProcessingPaymentLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showReceipt(Receipt receipt) {
        Intent intent = ReceiptActivity.newIntent(this, receipt);
        startActivity(intent);

        finish();
    }

    @Override
    public void showPaymentFailedError() {
        DialogUtils.showDialog(this, R.string.error, R.string.payment_failed);
    }

    @Override
    public void showInvalidPinError() {
        DialogUtils.showDialog(this, R.string.error, R.string.invalid_pin);
    }

    @Override
    public void showNetworkError() {
        DialogUtils.showDialog(this, R.string.error, R.string.unexpected_error);
    }

    @Override
    public void showInvalidDataError() {
        DialogUtils.customAlertDialogBuilder(this, R.string.invalid_payment_data).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }

    @Override
    public void showInsufficientBalanceError() {
        DialogUtils.showDialog(this, R.string.error, R.string.insufficient_balance_error);
    }

    @Override
    public void showTipChangeNotAllowedError() {
        DialogUtils.showDialog(this, R.string.error, R.string.error_tip_change_not_allowed);
    }

    private class AmountInputFilter implements InputFilter {
        private double min, max;

        AmountInputFilter(double min, double max) {
            this.min = min;
            this.max = max;
        }

        void setMax(double max) {
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String result = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

            if (result.indexOf(".") != result.lastIndexOf(".")) {
                return "";
            }

            double input = parseAmount(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;

            return null;
        }

        private boolean isInRange(double a, double b, double c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
