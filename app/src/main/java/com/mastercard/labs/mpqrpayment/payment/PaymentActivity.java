package com.mastercard.labs.mpqrpayment.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.CardsArrayAdapter;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.Receipt;
import com.mastercard.labs.mpqrpayment.receipt.ReceiptActivity;
import com.mastercard.labs.mpqrpayment.utils.DialogUtils;
import com.mastercard.labs.mpqrpayment.utils.DrawableUtils;
import com.mastercard.labs.mpqrpayment.utils.KeyboardUtils;
import com.mastercard.labs.mpqrpayment.view.AmountEditText;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentActivity extends AppCompatActivity implements PaymentContract.View, AmountEditText.AmountListener,
        TextView.OnEditorActionListener {
    public static String BUNDLE_PAYMENT_DATA_KEY = "paymentData";

    private PaymentContract.Presenter presenter;

    @BindView(R.id.txt_currency_value)
    TextView currencyTextView;

    @BindView(R.id.rl_amount)
    RelativeLayout amountLayout;

    @BindView(R.id.txt_amount)
    TextView amountTitleTextView;

    @BindView(R.id.txt_amount_value)
    AmountEditText amountEditText;

    @BindView(R.id.txt_tip)
    TextView tipTitleTextView;

    @BindView(R.id.txt_tip_value)
    AmountEditText tipEditText;

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

    private ProgressDialog progressDialog;

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

        amountEditText.setListener(this);
        tipEditText.setListener(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                presenter.confirmCancellation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.confirmCancellation();
    }

    @Override
    public void amountChanged(AmountEditText editText, double amount) {
        if (editText == amountEditText) {
            presenter.setAmount(amount);
        } else if (editText == tipEditText) {
            presenter.setTip(amount);
        }
    }

    @OnClick(value = R.id.rl_payment_card)
    public void changeCardBtnPressed() {
        presenter.selectCard();
    }

    @OnClick(value = R.id.btn_pay)
    public void payBtnPressed() {
        pay();
    }

    private void pay() {
        presenter.makePayment();
    }

    @Override
    public void setCurrency(String currency) {
        currencyTextView.setText(currency);
    }

    @Override
    public void setAmount(double amount) {
        amountEditText.setAmount(amount);
    }

    private void setTipHasPercentage(boolean hasPercentage) {
        tipEditText.setSuffix(hasPercentage ? " %" : "");
        tipEditText.setIntegerOnly(hasPercentage);
        tipEditText.setMinMax(new Pair<>(0.0, hasPercentage ? 100 : Double.MAX_VALUE));
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
        tipEditText.setAmount(amount);
    }

    @Override
    public void disableAmountChange() {
        toggleLayout(amountLayout, amountTitleTextView, amountEditText, false);
    }

    @Override
    public void enableAmountChange() {
        toggleLayout(amountLayout, amountTitleTextView, amountEditText, true);
    }

    @Override
    public void disableTipChange() {
        toggleLayout(tipLayout, tipTitleTextView, tipEditText, false);
        toggleTipEditable(false);
    }

    @Override
    public void enableTipChange() {
        toggleLayout(tipLayout, tipTitleTextView, tipEditText, true);
        toggleTipEditable(true);
    }

    private void toggleLayout(RelativeLayout layout, TextView titleTextView, EditText editText, boolean enabled) {
        if (enabled) {
            layout.setBackgroundColor(Color.TRANSPARENT);
            titleTextView.setTextColor(ContextCompat.getColor(this, R.color.colorWarmGrey));
            editText.setEnabled(true);
            editText.setFocusable(true);
        } else {
            // TODO: Remove line below edit text
            layout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDisabledLayoutColor));
            titleTextView.setTextColor(ContextCompat.getColor(this, R.color.colorEnabledLayoutColor));
            editText.setEnabled(false);
            editText.setFocusable(false);
            editText.clearFocus();
        }
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
        toggleTipEditable(false);
    }

    @Override
    public void showTipInformation() {
        topBorderTip.setVisibility(View.VISIBLE);
        tipLayout.setVisibility(View.VISIBLE);
        toggleTipEditable(true);
    }

    private void toggleTipEditable(boolean isEditable) {
        if (isEditable) {
            amountEditText.setImeActionLabel(getString(R.string.action_next), EditorInfo.IME_ACTION_NEXT);
            amountEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            amountEditText.setOnEditorActionListener(null);
            tipEditText.setOnEditorActionListener(this);
        } else {
            amountEditText.setImeActionLabel(getString(R.string.pay), EditorInfo.IME_ACTION_GO);
            amountEditText.setImeOptions(EditorInfo.IME_ACTION_GO);

            amountEditText.setOnEditorActionListener(this);
            tipEditText.setOnEditorActionListener(null);
        }
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
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);

        int padding = getResources().getDimensionPixelSize(R.dimen.size_14);
        layout.setPadding(padding, 0, padding, 0);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(pinLength)});

        layout.addView(input, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.enter_pin_title)
                .setMessage(R.string.enter_pin_message)
                .setView(layout)
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
        KeyboardUtils.hideKeyboard(this);

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

    @Override
    public void showCancelDialog() {
        DialogUtils.customAlertDialogBuilder(this, R.string.ask_cancel_confirmation).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                presenter.cancelFlow();
            }
        }).show();
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        }

        if (actionId == R.id.action_pay || actionId == EditorInfo.IME_NULL) {
            KeyboardUtils.hideKeyboard(this);
            pay();
            return true;
        }
        return false;
    }
}
