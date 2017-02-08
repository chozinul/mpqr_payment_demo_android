package com.mastercard.labs.mpqrpayment.merchant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mastercard.labs.mpqrpayment.R;

public class MerchantActivity extends AppCompatActivity implements MerchantContract.View {

    MerchantContract.Presenter presenter;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MerchantActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        presenter = new MerchantPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.start();
    }
}
