package com.mastercard.labs.mpqrpayment.merchant;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/8/17
 */
public interface MerchantContract {
    interface View extends BaseView<Presenter> {

        void showInvalidMerchantError();

        void showPaymentActivity(PaymentData paymentData);

        void hideMerchantInfo();

        void showInfoProgress();

        void hideInfoProgress();

        void showNetworkError();

        void showMerchantInfo(String name, String city);

        void explainMerchantCode();
    }

    interface Presenter extends BasePresenter {

        void moveToNextStep();

        void updateMerchantCode(String code);

        void explainMerchantCode();
    }
}
