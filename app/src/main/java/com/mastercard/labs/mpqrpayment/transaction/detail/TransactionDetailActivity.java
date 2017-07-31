package com.mastercard.labs.mpqrpayment.transaction.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.utils.DialogUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kaile on 17/7/17.
 */

public class TransactionDetailActivity extends AppCompatActivity implements TransactionDetailContract.View {
    private static final String BUNDLE_REFERENCE_ID_KEY = "referenceId";

    private TransactionDetailContract.Presenter mPresenter;
    private String mReferenceId;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.txt_amount_value)
    TextView amountTextView;

    @BindView(R.id.txt_tip_value)
    TextView tipTextView;

    @BindView(R.id.txt_transaction_date)
    TextView transactionDateTextView;

    @BindView(R.id.txt_invoice_value)
    TextView invoiceTextView;

    @BindView(R.id.txt_reference_value)
    TextView referenceTextView;

    @BindView(R.id.txt_merchant_name)
    TextView merchantTextView;

    public static Intent newIntent(Context context, String referenceId) {
        Intent intent = new Intent(context, TransactionDetailActivity.class);
        intent.putExtra(BUNDLE_REFERENCE_ID_KEY, referenceId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mReferenceId = savedInstanceState.getString(BUNDLE_REFERENCE_ID_KEY);
        } else {
            mReferenceId = getIntent().getStringExtra(BUNDLE_REFERENCE_ID_KEY);
        }

        mPresenter = new TransactionDetailPresenter(this, RealmDataSource.getInstance(), mReferenceId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        mReferenceId = intent.getStringExtra(BUNDLE_REFERENCE_ID_KEY);
        mPresenter = new TransactionDetailPresenter(this, RealmDataSource.getInstance(), mReferenceId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_REFERENCE_ID_KEY, mReferenceId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showInvalidTransactionError() {
        DialogUtils.customAlertDialogBuilder(this, R.string.invalid_pin).setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }

    @Override
    public void setTotalAmount(double total, String currency) {
        amountTextView.setText(String.format(Locale.getDefault(), "%s %,.2f", currency, total));
    }

    @Override
    public void setTip(double tipAmount, String currency) {
        tipTextView.setText(String.format(Locale.getDefault(), "%s %,.2f", currency, tipAmount));
    }

    @Override
    public void setTransactionDate(String dateString) {
        transactionDateTextView.setText(dateString);
    }

    @Override
    public void setInvoiceNumber(String invoiceNumber) {
        invoiceTextView.setText(invoiceNumber);
    }

    @Override
    public void setMerchantName(String merchantName) {
        merchantTextView.setText(merchantName);
    }

    @Override
    public void setReferenceId(String referenceId) {
        referenceTextView.setText(referenceId);
    }
}