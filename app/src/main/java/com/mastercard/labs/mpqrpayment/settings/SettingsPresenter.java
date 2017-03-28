package com.mastercard.labs.mpqrpayment.settings;

/**
 * Created by kaile on 22/3/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.Settings;
import com.mastercard.labs.mpqrpayment.utils.CalculateCode;


public class SettingsPresenter implements SettingsContract.Presenter {


    private SettingsContract.View mView;
    private Context mContext;
    private List<Settings> allSettings;
    private ArrayList<SettingsMerchant> mMerchants;
    private Settings currentSettings;
    private SharedPreferences sharedPref;

    SettingsPresenter(SettingsContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }


    public class SettingsMerchant {
        String merchantName;
        String merchantCard;
        String merchantId;

        public SettingsMerchant(String name, String card, String id) {
            merchantName = name;
            merchantCard = card;
            merchantId = id;
        }
    }

    @Override
    public void start() {

        populateView();
    }

    private void populateView() {

        mMerchants = new ArrayList<>();
        allSettings = new ArrayList<>();
        loadMerchantsList();

        int index =0;
        for(SettingsMerchant merchant: mMerchants) {
            allSettings.add(new Settings(merchant.merchantName, formattedCardNumber(merchant.merchantCard), merchant.merchantId, index, true));
            index++;
        }

        mView.showSettings(allSettings);
    }

    private void loadMerchantsList(){

        sharedPref = mContext.getSharedPreferences( mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String,?> keys = sharedPref.getAll();


        String[] merchant;
        for(Map.Entry<String,?> entry : keys.entrySet()){
            merchant = (entry.getValue().toString()).split("\\|");
            SettingsMerchant newMerchant = new SettingsMerchant(merchant[0], merchant[1], merchant[2]);
            mMerchants.add(newMerchant);
        }
    }

    private String formattedCardNumber(String cardNumber) {
        cardNumber = cardNumber.replace(" ", "");

        String formatted = "";
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i % 4 == 0) {
                formatted += " ";
            }
            formatted += cardNumber.charAt(i);
        }

        return formatted.trim();
    }

    @Override
    public void settingsSelected(Settings settings) {
        if (!settings.isEditable()) {
            currentSettings = null;
            return;
        }
             mView.showMerchantEditor(settings.getName(), settings.getCard());
             currentSettings = settings;
    }


    @Override
    public void settingsDelete(Settings settings) {
        if (settings == null) {
            currentSettings = null;
            return;
        }

        SharedPreferences.Editor editor = sharedPref.edit();


            SettingsMerchant merchant = mMerchants.get(settings.getIndex());
            String merchantName = merchant.merchantName;
            if (merchant != null)
            {editor.remove(merchant.merchantId);
             editor.commit();
    }

            populateView();

            Toast.makeText(mContext, merchantName.toUpperCase() + " has been removed", Toast.LENGTH_SHORT).show();
            currentSettings = null;
    }

    @Override
    public void updateMerchant(String name, String card) {
        if ((name == null || card == null) || (name.isEmpty() || card.isEmpty())) {
            return;
        }

        String code =  CalculateCode.calculate8digit(card);
        SharedPreferences.Editor editor = sharedPref.edit();

            if (currentSettings != null) {
                SettingsMerchant merchant = mMerchants.get(currentSettings.getIndex());
                mMerchants.set(currentSettings.getIndex(), merchant);
                editor.remove(merchant.merchantId);}

        editor.putString(code,name+"|"+card+"|"+code);
        editor.commit();


        populateView();

        Toast.makeText(mContext, "Updated", Toast.LENGTH_SHORT).show();
        currentSettings = null;
    }


    public void setCurrenttoNull()
    {currentSettings = null;}


    public boolean checkCurrentExist()
    {if (currentSettings != null)
    return true;
    return false;}
}
