package com.mastercard.labs.mpqrpayment.transaction.detail;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;

/**
 * Created by kaile on 17/7/17.
 */

public interface TransactionDetailContract {
    interface View extends BaseView<Presenter> {

        void showInvalidTransactionError();

        void setTotalAmount(double total, String currency);

        void setTip(double tipAmount, String currency);

        void setTransactionDate(String dateString);

        void setInvoiceNumber(String invoiceNumber);

        void setMerchantName(String merchantName);

        void setReferenceId(String referenceId);
    }

    interface Presenter extends BasePresenter {

    }
}
