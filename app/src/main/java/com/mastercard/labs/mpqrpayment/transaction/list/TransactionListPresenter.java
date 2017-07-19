package com.mastercard.labs.mpqrpayment.transaction.list;

import com.mastercard.labs.mpqrpayment.data.DataSource;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.Transaction;
import com.mastercard.labs.mpqrpayment.data.model.User;

import java.util.List;

/**
 * Created by kaile on 17/7/17.
 */

public class TransactionListPresenter implements TransactionListContract.Presenter {
    private TransactionListContract.View mView;
    private DataSource mDataSource;
    private long mId;
    private int cardId;
    private String maskedId;
    private String acquirer;

    private User mUser;
    private List<Transaction> mTransactions;

    TransactionListPresenter(TransactionListContract.View view, DataSource dataSource, long merchantId, int cardId) {
        this.mView = view;
        this.mDataSource = dataSource;
        this.mId = merchantId;
        this.cardId = cardId;
    }

    @Override
    public void start() {
        mUser = mDataSource.getUser(mId);
        if (mUser == null) {
            mView.showInvalidUser();
            return;
        }

        PaymentInstrument selected = mUser.getPaymentInstruments().get(cardId);
        maskedId = selected.getMaskedIdentifier();
        acquirer = selected.getAcquirerName();
        mTransactions = mDataSource.getTransactions(mId, maskedId);
        populateView();
    }

    private void populateView() {

        mView.setTexts(maskedId, acquirer);
        mView.setTransactions(mTransactions);
    }
}
