package com.mastercard.labs.mpqrpayment.payment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.database.RealmDataSource;
import com.mastercard.labs.mpqrpayment.database.model.CardType;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class PaymentActivity extends AppCompatActivity implements PaymentContract.View {
    public static String BUNDLE_PP_KEY = "pushPaymentData";
    public static String BUNDLE_CARD_ID_KEY = "cardId";

    private PaymentContract.Presenter presenter;

    @BindView(R.id.txt_currency_value)
    TextView currencyTextView;

    @BindView(R.id.txt_amount_value)
    EditText amountEditText;

    @BindView(R.id.txt_tip)
    TextView tipTitleTextView;

    @BindView(R.id.txt_tip_value)
    EditText tipEditText;

    @BindView(R.id.txt_total_amount)
    TextView totalAmountTextView;

    @BindView(R.id.txt_merchant_name)
    TextView merchantNameTextView;

    @BindView(R.id.txt_merchant_city)
    TextView merchantCityTextView;

    @BindView(R.id.txt_payment_card)
    TextView paymentCardTextView;

    private boolean blockAmountTextViewChange;
    private boolean blockTipTextViewChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String paymentDataString;
        Long cardId;
        if (savedInstanceState != null) {
            paymentDataString = savedInstanceState.getString(BUNDLE_PP_KEY);
            cardId = savedInstanceState.getLong(BUNDLE_CARD_ID_KEY);
        } else {
            paymentDataString = getIntent().getStringExtra(BUNDLE_PP_KEY);
            cardId = getIntent().getLongExtra(BUNDLE_CARD_ID_KEY, -1L);
        }

        presenter = new PaymentPresenter(this, RealmDataSource.getInstance());
        presenter.setPushPaymentDataString(paymentDataString);
        presenter.setCardId(cardId);

        amountEditText.setFilters(new InputFilter[]{new AmountInputFilter()});
        tipEditText.setFilters(new InputFilter[]{new AmountInputFilter()});
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();
    }

    @OnTextChanged(value = R.id.txt_amount_value, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAmountChanged(Editable amountText) {
        if (blockAmountTextViewChange) {
            return;
        }

        blockAmountTextViewChange = true;

        double amount = validateAmount(amountText);

        blockAmountTextViewChange = false;

        presenter.setAmount(amount / 100);
    }

    @OnTextChanged(value = R.id.txt_tip_value, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTipChanged(Editable tipText) {
        if (blockTipTextViewChange) {
            return;
        }

        blockTipTextViewChange = true;

        double tip = validateAmount(tipText);

        blockTipTextViewChange = false;

        presenter.setTip(tip / 100);
    }

    private double validateAmount(Editable amountText) {
        double amount = 0;
        try {
            String amountString = amountText.toString();
            amount = Double.parseDouble(amountString);
            int count = amountString.length() - amountString.indexOf(".") - 1;
            amount *= Math.pow(10, count);

            amountText.clear();

            amountText.append(String.format(Locale.getDefault(), "%.2f", amount / 100));
        } catch (Exception ex) {
            amountText.clear();
            amountText.append("0.00");
        }

        return amount;
    }

    @Override
    public void setCurrency(String currency) {
        currencyTextView.setText(currency);
    }

    @Override
    public void setAmount(double amount) {
        blockAmountTextViewChange = true;
        amountEditText.setText(String.format(Locale.getDefault(), "%.2f", amount));
        blockAmountTextViewChange = false;
    }

    @Override
    public void setFlatConvenienceFee(double fee) {
        tipTitleTextView.setText(R.string.flat_convenience_fee);
        setTipText(String.format(Locale.getDefault(), "%.2f", fee));
    }

    @Override
    public void setPercentageConvenienceFee(double feePercentage) {
        tipTitleTextView.setText(R.string.percentage_convenience_fee);
        setTipText(String.format(Locale.getDefault(), "%.2f", feePercentage));
    }

    @Override
    public void setPromptToEnterTip(double tip) {
        tipTitleTextView.setText(R.string.tip);
        setTipText(String.format(Locale.getDefault(), "%.2f", tip));
    }

    private void setTipText(String text) {
        blockTipTextViewChange = true;
        tipEditText.setText(text);
        blockTipTextViewChange = false;
    }

    @Override
    public void disableTipChange() {
        tipEditText.setEnabled(false);
    }

    @Override
    public void enableTipChange() {
        tipEditText.setEnabled(true);
    }

    @Override
    public void setTotalAmount(double amount, String currencyCode) {
        totalAmountTextView.setText(String.format(Locale.getDefault(), "%s %,.2f", currencyCode, amount));
    }

    @Override
    public void hideTipInformation() {
        // TODO: Hide tip
    }

    @Override
    public void showTipInformation() {
        // TODO: Show tip
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
    public void setCardInfo(CardType cardType, String lastDigits) {
        @DrawableRes int imageId = 0;
        switch (cardType) {
            case MastercardBlack:
            case MastercardGold:
                imageId = R.drawable.mastercard_logo;
                break;
            case SavingsAccount:
                imageId = R.drawable.savings_account_logo;
            default:
                // TODO: Add default card logo
                break;
        }

        paymentCardTextView.setCompoundDrawablesWithIntrinsicBounds(imageId, 0, 0, 0);
        paymentCardTextView.setText(getString(R.string.pay_with_card, lastDigits));
    }

    private class AmountInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String result = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

            if (result.indexOf(".") != result.lastIndexOf(".")) {
                return "";
            }

            return null;
        }
    }
}
