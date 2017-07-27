package com.mastercard.labs.mpqrpayment.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements SettingsContract.View {
    public static String BUNDLE_USER_KEY = "userId";


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.switchNotifications)
    Switch notificationSwitch;

    @BindView(R.id.mobile_value)
    TextView mobileNumberValue;

    private long mUserId;
    private SettingsContract.Presenter mPresenter;

    public static Intent newIntent(Context context, long userId) {
        Intent intent = new Intent(context, SettingsActivity.class);

        intent.putExtra(BUNDLE_USER_KEY, userId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mUserId = savedInstanceState.getLong(BUNDLE_USER_KEY, -1);
        } else {
            mUserId = getIntent().getLongExtra(BUNDLE_USER_KEY, -1);
        }
        mPresenter = new SettingsPresenter(this);

        updateSwitchValue();
        updateMobileField();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(BUNDLE_USER_KEY, mUserId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    @OnClick(value = R.id.switchNotifications)
    public void toggleSwitch() {
        mPresenter.setNotificationPreference(notificationSwitch.isChecked());
    }


    @Override
    @OnClick(value = R.id.mobile_layout)
    public void showMerchantMobileEditor() {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);

        int padding = getResources().getDimensionPixelSize(R.dimen.size_14);
        layout.setPadding(padding, 0, padding, 0);

        final EditText input = new EditText(layout.getContext());
        input.setText(mPresenter.getMobileNumber());

        layout.addView(input, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.merchant_mobile)
                .setMessage(R.string.enter_merchant_mobile)
                .setView(layout)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mPresenter.setMobileNumber(input.getText().toString());
                    }
                }).create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();

        input.setSelection(input.length(), input.length());

    }

    @Override
    public void updateMobileField() {
        String number = mPresenter.getMobileNumber();
        if (number.isEmpty())
            number = "No number entered";
        mobileNumberValue.setText(number);
    }

    @Override
    public void updateSwitchValue() {
      notificationSwitch.setChecked(mPresenter.getNotificationPreference());
    }

}
