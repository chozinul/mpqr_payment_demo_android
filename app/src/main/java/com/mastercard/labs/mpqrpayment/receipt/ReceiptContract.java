package com.mastercard.labs.mpqrpayment.receipt;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/3/17
 */
public interface ReceiptContract {
    interface View extends BaseView {

        void setTotalAmount(Double totalAmount, String currencyCode);

        void setMerchantName(String merchantName);

        void setMerchantCity(String merchantCity);

        void setAmount(Double amount);

        void setTipAmount(Double tipAmount);

        void setMaskedPan(String maskedPan);

        void showTipInformation();

        void hideTipInformation();
    }

    interface Presenter extends BasePresenter {

    }
}
