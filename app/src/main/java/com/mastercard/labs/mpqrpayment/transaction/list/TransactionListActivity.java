package com.mastercard.labs.mpqrpayment.transaction.list;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.TransactionsAdapter;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.Transaction;
import com.mastercard.labs.mpqrpayment.transaction.detail.TransactionDetailActivity;
import com.mastercard.labs.mpqrpayment.transaction.overview.TransactionOverviewFragment;
import com.mastercard.labs.mpqrpayment.utils.DialogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kaile on 17/7/17.
 */

public class TransactionListActivity extends AppCompatActivity implements TransactionListContract.View, TransactionsAdapter.TransactionItemListener {
    public static String BUNDLE_USER_KEY = "userId";
    public static String BUNDLE_CARD_ID = "maskedIdentifier";

    @BindView(R.id.rv_transactions)
    RecyclerView mTransactionsRecyclerView;

    @BindView(R.id.no_transactions_text)
    TextView noTransactionsTextView;

    private long mUserId;
    private int mCardId;
    private TransactionListContract.Presenter mPresenter;
    private TransactionsAdapter mTransactionsAdapter;

    public static Intent newIntent(Context context, long userId, int position) {
        Intent intent = new Intent(context, TransactionListActivity.class);

        intent.putExtra(BUNDLE_USER_KEY, userId);
        intent.putExtra(BUNDLE_CARD_ID, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTransactionsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTransactionsRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mTransactionsRecyclerView.addItemDecoration(dividerItemDecoration);

        if (savedInstanceState != null) {

            mUserId = savedInstanceState.getLong(BUNDLE_USER_KEY, -1);
            mCardId = savedInstanceState.getInt(BUNDLE_CARD_ID);
        } else {

            mUserId = getIntent().getLongExtra(BUNDLE_USER_KEY, -1);
            mCardId = getIntent().getIntExtra(BUNDLE_CARD_ID, -1);
        }

        mPresenter = new TransactionListPresenter(this, RealmDataSource.getInstance(), mUserId, mCardId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(BUNDLE_USER_KEY, mUserId);
        outState.putInt(BUNDLE_CARD_ID, mCardId);

        super.onSaveInstanceState(outState);
    }

    private TransactionOverviewFragment getTransactionOverviewFragment() {
        return (TransactionOverviewFragment) getSupportFragmentManager().findFragmentById(R.id.frg_transaction_overview);
    }

    @Override
    public void showInvalidUser() {
        DialogUtils.customAlertDialogBuilder(this, R.string.unexpected_error).setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }

    @Override
    public void setTexts(String number, String name) {
        TransactionOverviewFragment transactionOverviewFragment = getTransactionOverviewFragment();
        if (transactionOverviewFragment == null) {
            return;
        }
        transactionOverviewFragment.setCardNumber(number);
        transactionOverviewFragment.setAcquirerName(name);
    }

    @Override
    public void setTransactions(List<Transaction> transactions) {
        mTransactionsAdapter = new TransactionsAdapter(transactions, this);
        mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);
        toggleEmptyView(transactions.isEmpty());
    }

    @Override
    public void onTransactionItemClicked(Transaction transaction) {
        Intent intent = TransactionDetailActivity.newIntent(this, transaction.getReferenceId());
        startActivity(intent);
    }

    public void toggleEmptyView(boolean isEmpty) {
        if (isEmpty)
            noTransactionsTextView.setVisibility(View.VISIBLE);
        else noTransactionsTextView.setVisibility(View.GONE);
    }
}
