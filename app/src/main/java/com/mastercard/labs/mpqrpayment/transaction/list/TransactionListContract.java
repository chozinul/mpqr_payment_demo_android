package com.mastercard.labs.mpqrpayment.transaction.list;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;
import com.mastercard.labs.mpqrpayment.data.model.Transaction;

import java.util.List;

/**
 * Created by kaile on 17/7/17.
 */

public interface TransactionListContract {
    interface View extends BaseView<Presenter> {

        void showInvalidUser();

        void setTexts(String number, String name);

        void setTransactions(List<Transaction> transactions);
    }

    interface Presenter extends BasePresenter {
    }
}
