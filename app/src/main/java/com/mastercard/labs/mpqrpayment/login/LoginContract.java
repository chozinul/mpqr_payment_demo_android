package com.mastercard.labs.mpqrpayment.login;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/7/17
 */
public interface LoginContract {
    interface View extends BaseView<Presenter> {

        void setInvalidPinError();

        void clearAccessCodeError();

        void clearPinError();

        void showProgress();

        void hideProgress();

        void startMainFlow();

        void setIncorrectPin();

        void showNetworkError();

        void setInvalidAccessCode();

        void setAccessCode(String accessCode);
    }

    interface Presenter extends BasePresenter {

        void login(String accessCode, String pin);
    }
}
