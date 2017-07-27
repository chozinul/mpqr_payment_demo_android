package com.mastercard.labs.mpqrpayment.transaction.detail;

import com.mastercard.labs.mpqrpayment.data.DataSource;
import com.mastercard.labs.mpqrpayment.data.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kaile on 17/7/17.
 */

class TransactionDetailPresenter implements TransactionDetailContract.Presenter {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault());

    private TransactionDetailContract.View mView;
    private DataSource mDataSource;
    private String mReferenceId;

    private Transaction mTransaction;

    TransactionDetailPresenter(TransactionDetailContract.View view, DataSource dataSource, String referenceId) {
        this.mView = view;
        this.mDataSource = dataSource;
        this.mReferenceId = referenceId;
    }

    @Override
    public void start() {
        mTransaction = mDataSource.getTransaction(mReferenceId);

        populateView();
    }

    private void populateView() {
        if (mTransaction == null) {
            mView.showInvalidTransactionError();
            return;
        }

        String currency = "";
        if (mTransaction.getCurrencyCode() != null) {
            currency = mTransaction.getCurrencyCode().toString();
        }

        mView.setTotalAmount(mTransaction.getTotal(), currency);
        mView.setTip(mTransaction.getTipAmount(), currency);
        mView.setTransactionDate(dateFormat.format(mTransaction.getTransactionDate()));
        mView.setInvoiceNumber(mTransaction.getInvoiceNumber());
        mView.setMerchantName(mTransaction.getMerchantName());
        mView.setReferenceId(mReferenceId);
    }
}

