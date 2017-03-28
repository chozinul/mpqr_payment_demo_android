package com.mastercard.labs.mpqrpayment.settings;

/**
 * Created by kaile on 22/3/17.
 */

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;
import com.mastercard.labs.mpqrpayment.data.model.Settings;
 import java.util.List;


public interface SettingsContract {
    interface View extends BaseView<Presenter> {

        void showSettings(List<Settings> allSettings);

        void showMerchantEditor(String name, String code);

    }

    interface Presenter extends BasePresenter {

        void settingsSelected(Settings settings);

        void updateMerchant(String value, String card);

        void setCurrenttoNull();

        boolean checkCurrentExist();

        void settingsDelete(Settings settings);
    }
}
