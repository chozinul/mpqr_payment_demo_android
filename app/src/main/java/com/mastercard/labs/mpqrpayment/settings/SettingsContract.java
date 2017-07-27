package com.mastercard.labs.mpqrpayment.settings;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;

/**
 * Created by kaile on 26/7/17.
 */

public class SettingsContract {

    interface View extends BaseView {

        void toggleSwitch();
        void showMerchantMobileEditor();
        void updateMobileField();
        void updateSwitchValue();
    }

    interface Presenter extends BasePresenter {

        String getMobileNumber();
        void setMobileNumber(String number);
        boolean getNotificationPreference();
        void setNotificationPreference(boolean preference);
    }
}
