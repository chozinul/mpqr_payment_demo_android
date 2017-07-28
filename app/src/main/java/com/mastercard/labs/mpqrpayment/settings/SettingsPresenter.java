package com.mastercard.labs.mpqrpayment.settings;

import com.mastercard.labs.mpqrpayment.utils.PreferenceManager;

/**
 * Created by kaile on 26/7/17.
 */

public class SettingsPresenter implements SettingsContract.Presenter {
    private SettingsContract.View mView;

    SettingsPresenter(SettingsContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {
    }

    @Override
    public String getMobileNumber() {
        return PreferenceManager.getInstance().getMobileValue();
    }

    @Override
    public void setMobileNumber(String number) {
        PreferenceManager.getInstance().setMobileValue(number);
        mView.updateMobileField();
    }

    @Override
    public boolean getNotificationPreference() {
        return PreferenceManager.getInstance().getNotificationPreference();
    }

    @Override
    public void setNotificationPreference(boolean preference) {
        PreferenceManager.getInstance().setNotificationPreference(preference);
    }

}
