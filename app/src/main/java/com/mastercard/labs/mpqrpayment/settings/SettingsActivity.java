package com.mastercard.labs.mpqrpayment.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.SettingsAdapter;
import com.mastercard.labs.mpqrpayment.data.model.Settings;


public class SettingsActivity extends AppCompatActivity implements SettingsContract.View, SettingsAdapter.SettingsItemListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_settings)
    RecyclerView mSettingsRecyclerView;

    private SettingsContract.Presenter mPresenter;
    private SettingsAdapter mSettingsAdapter;



    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSettingsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mSettingsRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        mSettingsRecyclerView.addItemDecoration(dividerItemDecoration);


        mPresenter = new SettingsPresenter(this, this.getBaseContext());

    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void showSettings(List<Settings> allSettings) {
        mSettingsAdapter = new SettingsAdapter(allSettings, this);
        mSettingsRecyclerView.setAdapter(mSettingsAdapter);
    }

    @Override
    public void showMerchantEditor(String name, String code) {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);

        int padding = getResources().getDimensionPixelSize(R.dimen.size_14);
        layout.setPadding(padding, 0, padding, 0);


        final TextView nametext = new TextView(layout.getContext());
        nametext.setText(R.string.enter_merchant_name);
        nametext.setTextSize(15);
        nametext.setPadding(10, 50, 0, 0);
        final EditText input = new EditText(layout.getContext());
        input.setText(name);


        final TextView codetext = new TextView(layout.getContext());
        codetext.setText(R.string.enter_merchant_card);
        codetext.setTextSize(15);
        codetext.setPadding(10, 50, 0, 0);
        final EditText codeInput = new EditText(layout.getContext());
        codeInput.setText(code);
        codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        codeInput.addTextChangedListener(new FourDigitCardFormatWatcher());
        codeInput.setKeyListener(DigitsKeyListener.getInstance("0123456789 "));
        codeInput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16 + 3)});



        layout.addView(nametext, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(input, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        layout.addView(codetext, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(codeInput, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        String title;
        if(mPresenter.checkCurrentExist())
            title = "Edit Merchant";
        else title = "Add Merchant";

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
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
                    }
                }).create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();

        input.setSelection(input.length(), input.length());
        codeInput.setSelection(codeInput.length(), codeInput.length());

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Validation should be done in presenter
                if (!isValidCardNumber(codeInput.getText().toString().replace(" ", ""))) {
                    codeInput.setError(getString(R.string.error_invalid_card_number));
                } else {
                    dialog.dismiss();
                    mPresenter.updateMerchant(input.getText().toString(),codeInput.getText().toString().replace(" ", ""));
                }
            }
        });
    }


    public void onSettingsItemClicked(Settings settings) {
        mPresenter.settingsSelected(settings);
    }

    public void onSettingsItemDelete(Settings settings) {

        mPresenter.settingsDelete(settings);
    }

    private boolean isValidCardNumber(String cardNumber) {
        Pattern mastercardPattern = Pattern.compile("^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$");
        return mastercardPattern.matcher(cardNumber).matches();
    }

    static class FourDigitCardFormatWatcher implements TextWatcher {
        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Remove spacing char
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }

            // TODO: Remove to support other cards. For now only Mastercard is accepted
            if (s.length() == 0) {
                s.insert(0, "5");
            }
        }
    }


    @OnClick(value = R.id.btn_add)
    public void showAddDialog() {
        mPresenter.setCurrenttoNull();
        showMerchantEditor("","");
    }
}

