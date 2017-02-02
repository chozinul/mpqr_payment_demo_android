package com.mastercard.labs.mpqrpayment.activity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.MainApplication;
import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.CardPagerAdapter;
import com.mastercard.labs.mpqrpayment.database.model.Card;
import com.mastercard.labs.mpqrpayment.database.model.User;
import com.mastercard.labs.mpqrpayment.payment.PaymentActivity;
import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.scan.PPIntentIntegrator;
import com.mastercard.mpqr.pushpayment.scan.constant.PPIntents;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    public static String BUNDLE_USER_KEY = "user_id";

    @BindView(R.id.pager_container)
    PagerContainer pagerContainer;

    @BindView(R.id.txtAvailableBalanceAmount)
    TextView availableBalanceTextView;

    Realm realm;

    private Long userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        userId = getIntent().getLongExtra(BUNDLE_USER_KEY, -1L);
        // TODO: Only for debugging purposes till Login screen is implemented.
        userId = MainApplication.loggedInUserId();

        if (userId == -1) {
            // TODO: Show error
            return;
        }

        realm = Realm.getDefaultInstance();

        pagerContainer.setOverlapEnabled(true);

        final ViewPager viewPager = pagerContainer.getViewPager();
        CardPagerAdapter pagerAdapter = new CardPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        new CoverFlow.Builder().with(viewPager)
                .scale(0.3f)
                .pagerMargin(getResources().getDimensionPixelSize(R.dimen.overlap_pager_margin))
                .spaceSize(0f)
                .build();

        invalidateViews();
    }

    @Override
    protected void onDestroy() {
        realm.removeAllChangeListeners();
        realm.close();

        super.onDestroy();
    }

    // Page listener methods
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Card card = this.user.getCards().get(position);
        updateBalance(card);
    }

    private void updateBalance(Card card) {
        CurrencyCode currencyCode = CurrencyCode.fromNumericCode(card.getCurrencyNumericCode());
        String balanceAmount = String.format(Locale.getDefault(), "%.2f", card.getBalance());
        if (currencyCode != null) {
            try {
                Currency currency = Currency.getInstance(currencyCode.toString());
                NumberFormat format = NumberFormat.getCurrencyInstance();
                format.setCurrency(currency);
                balanceAmount = format.format(card.getBalance());
            } catch (Exception ex) {
                // Ignore this
            }
        }

        availableBalanceTextView.setText(balanceAmount);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void invalidateViews() {
        if (user != null) {
            user.removeChangeListeners();
        }

        user = realm.where(User.class).equalTo("userId", userId).findFirst();
        if (user == null) {
            // TODO: Show error
            return;
        }

        user.addChangeListener(new RealmChangeListener<User>() {
            @Override
            public void onChange(User result) {
                refreshCards(result);
            }
        });

        refreshCards(user);
    }

    private void refreshCards(User user) {
        this.user = realm.copyFromRealm(user);

        final ViewPager viewPager = pagerContainer.getViewPager();
        CardPagerAdapter pagerAdapter = (CardPagerAdapter) viewPager.getAdapter();

        pagerAdapter.setCards(Collections.unmodifiableList(this.user.getCards()));
        pagerAdapter.notifyDataSetChanged();

        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());

        if (user.getCards().size() > 0) {
            updateBalance(user.getCards().get(0));
            // Manually setting the first View to be elevated
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                    ViewCompat.setElevation(fragment.getView(), 8.0f);
                }
            });
        }
    }

    @OnClick(R.id.scan_qr_button)
    public void scanFromCamera() {
        // TODO: For testing only
        showPaymentActivity("00020101021204154600678934521435204520453033565405100.05502015802US5910Merchant A6009Singapore62280305A600804030000708457843126304534B");

//        IntentIntegrator integrator = new PPIntentIntegrator(this);
//        integrator.setOrientationLocked(false);
//        integrator.setBeepEnabled(false);
//        integrator.setPrompt("Scan QR Code");
//        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            IntentResult result = PPIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            PushPaymentData pushPaymentData = (PushPaymentData) data.getSerializableExtra(PPIntents.PUSH_PAYMENT_DATA);
            if (pushPaymentData != null) {
                // TODO: Show UI
                showPaymentActivity(pushPaymentData.generatePushPaymentString());
            } else {
                // TODO: Show error
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (data != null) {
                FormatException e = (FormatException) data.getSerializableExtra(PPIntents.PARSE_ERROR);
                if (e != null) {
                    // TODO: Show error
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPaymentActivity(String pushPaymentDataString) {
        Bundle bundle = new Bundle();
        bundle.putString(PaymentActivity.BUNDLE_PP_KEY, pushPaymentDataString);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
