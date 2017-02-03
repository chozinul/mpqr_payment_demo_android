package com.mastercard.labs.mpqrpayment.payment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.CardsArrayAdapter;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.Card;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PaymentActivity extends AppCompatActivity implements PaymentContract.View {
    public static String BUNDLE_PP_KEY = "pushPaymentData";
    public static String BUNDLE_USER_ID_KEY = "userId";
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

    private String paymentDataString;
    private Long userId;
    private Long cardId;

    private boolean blockAmountTextViewChange;
    private boolean blockTipTextViewChange;

    public static Intent newIntent(Context context, String pushPaymentDataString, Long userId, Long cardId) {
        Bundle bundle = new Bundle();
        bundle.putString(PaymentActivity.BUNDLE_PP_KEY, pushPaymentDataString);
        bundle.putLong(PaymentActivity.BUNDLE_USER_ID_KEY, userId);
        bundle.putLong(PaymentActivity.BUNDLE_CARD_ID_KEY, cardId);

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
            paymentDataString = savedInstanceState.getString(BUNDLE_PP_KEY);
            userId = savedInstanceState.getLong(BUNDLE_USER_ID_KEY);
            cardId = savedInstanceState.getLong(BUNDLE_CARD_ID_KEY);
        } else {
            paymentDataString = getIntent().getStringExtra(BUNDLE_PP_KEY);
            userId = getIntent().getLongExtra(BUNDLE_USER_ID_KEY, -1L);
            cardId = getIntent().getLongExtra(BUNDLE_CARD_ID_KEY, -1L);
        }

        presenter = new PaymentPresenter(this, RealmDataSource.getInstance(), userId);
        presenter.setPushPaymentDataString(paymentDataString);
        presenter.setCardId(cardId);

        amountEditText.setFilters(new InputFilter[]{new AmountInputFilter()});
        tipEditText.setFilters(new InputFilter[]{new AmountInputFilter()});
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(BUNDLE_PP_KEY, paymentDataString);
        outState.putLong(BUNDLE_USER_ID_KEY, userId);
        outState.putLong(BUNDLE_CARD_ID_KEY, cardId);
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

    @OnClick(value = R.id.rl_payment_card)
    public void changeCardBtnPressed() {
        presenter.selectCard();
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
    public void setCard(Card card) {
        @DrawableRes int imageId = 0;
        switch (card.getCardType()) {
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
        paymentCardTextView.setText(getString(R.string.pay_with_card, card.getMaskedPan()));
    }

    @Override
    public void showCardSelection(final List<Card> cards, int selectedCardIdx) {
        final CardsArrayAdapter adapter = new CardsArrayAdapter(this, cards);
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
                        presenter.setCardId(cards.get(adapter.getSelectedIndex()).getCardId());
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
