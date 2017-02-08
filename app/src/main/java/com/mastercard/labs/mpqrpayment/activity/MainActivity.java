package com.mastercard.labs.mpqrpayment.activity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.CardPagerAdapter;
import com.mastercard.labs.mpqrpayment.data.model.Merchant;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.login.LoginActivity;
import com.mastercard.labs.mpqrpayment.merchant.MerchantActivity;
import com.mastercard.labs.mpqrpayment.network.LoginManager;
import com.mastercard.labs.mpqrpayment.network.ServiceGenerator;
import com.mastercard.labs.mpqrpayment.payment.PaymentActivity;
import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;
import com.mastercard.labs.mpqrpayment.utils.DialogUtils;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.scan.PPIntentIntegrator;
import com.mastercard.mpqr.pushpayment.scan.constant.PPIntents;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    public static String BUNDLE_USER_KEY = "userId";
    public static String BUNDLE_SELECTED_CARD_IDX = "selectedCardIdx";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pager_container)
    PagerContainer pagerContainer;

    @BindView(R.id.txtAvailableBalanceAmount)
    TextView availableBalanceTextView;

    private ProgressDialog progressDialog;

    Realm realm;

    private Long userId;
    private User user;
    private int selectedCardIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        userId = getIntent().getLongExtra(BUNDLE_USER_KEY, -1L);
        // TODO: Only for debugging purposes till Login screen is implemented.
        userId = LoginManager.getInstance().getLoggedInUserId();

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

        if (savedInstanceState != null) {
            selectedCardIdx = savedInstanceState.getInt(BUNDLE_SELECTED_CARD_IDX, -1);
        }

        invalidateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_SELECTED_CARD_IDX, selectedCardIdx);
    }

    @Override
    protected void onDestroy() {
        if (user != null) {
            user.removeChangeListeners();
        }

        realm.close();

        super.onDestroy();
    }

    // Page listener methods
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedCardIdx = position;
        updateBalance();
    }

    private void updateBalance() {
        PaymentInstrument selectedPaymentInstrument = user.getPaymentInstruments().get(selectedCardIdx);

        String balanceAmount = CurrencyCode.formatAmount(selectedPaymentInstrument.getBalance(), selectedPaymentInstrument.getCurrencyNumericCode());

        availableBalanceTextView.setText(balanceAmount);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void invalidateViews() {
        if (user != null) {
            user.removeChangeListeners();
        }

        user = realm.where(User.class).equalTo("id", userId).findFirst();
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
        if (user.getPaymentInstruments().isEmpty()) {
            // TODO: Show error
            return;
        }

        if (selectedCardIdx == -1) {
            for (int i = 0; i < user.getPaymentInstruments().size(); i++) {
                if (user.getPaymentInstruments().get(i).isDefault()) {
                    selectedCardIdx = i;
                }
            }
        }

        PaymentInstrument selectedPaymentInstrument = user.getPaymentInstruments().get(selectedCardIdx);

        List<PaymentInstrument> paymentInstruments = user.getPaymentInstruments();

        final ViewPager viewPager = pagerContainer.getViewPager();
        CardPagerAdapter pagerAdapter = (CardPagerAdapter) viewPager.getAdapter();

        pagerAdapter.setPaymentInstruments(Collections.unmodifiableList(paymentInstruments));
        pagerAdapter.notifyDataSetChanged();

        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());

        if (selectedPaymentInstrument == null) {
            selectedPaymentInstrument = user.getPaymentInstruments().first();
        }

        viewPager.setCurrentItem(paymentInstruments.indexOf(selectedPaymentInstrument));
        updateBalance();

        // Manually setting the first View to be elevated
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, selectedCardIdx);
                if (fragment == null) {
                    fragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                }

                if (fragment != null) {
                    ViewCompat.setElevation(fragment.getView(), 8.0f);
                }
            }
        });
    }

    @OnClick(R.id.scan_qr_button)
    public void scanFromCamera() {
        // TODO: For testing only
        IntentIntegrator integrator = new PPIntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("Scan QR Code");
        integrator.setCaptureActivity(CustomizedPPCaptureActivity.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            IntentResult result = PPIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            PushPaymentData pushPaymentData = (PushPaymentData) data.getSerializableExtra(PPIntents.PUSH_PAYMENT_DATA);
            if (pushPaymentData != null) {
                // TODO: Show UI
                showPaymentActivity(pushPaymentData);
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
        } else if (resultCode == CustomizedPPCaptureActivity.RESULT_CANNOT_SCAN_QR) {
            showMerchantActivity();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPaymentActivity(PushPaymentData pushPaymentData) {
        PaymentData paymentData = paymentData(pushPaymentData);

        Intent intent = PaymentActivity.newIntent(this, paymentData);
        startActivity(intent);
    }

    private void showMerchantActivity() {
        Long userId = user.getId();
        PaymentInstrument selectedPaymentInstrument = user.getPaymentInstruments().get(selectedCardIdx);

        Intent intent = MerchantActivity.newIntent(this, userId, selectedPaymentInstrument.getId(), "356");
        startActivity(intent);
    }

    private PaymentData paymentData(PushPaymentData pushPaymentData) {
        PaymentData.TipInfo tipInfo = null;
        double tip = 0;
        if (pushPaymentData.getTipOrConvenienceIndicator() != null) {
            switch (pushPaymentData.getTipOrConvenienceIndicator()) {
                case PushPaymentData.TipConvenienceIndicator.FLAT_CONVENIENCE_FEE:
                    tipInfo = PaymentData.TipInfo.FLAT_CONVENIENCE_FEE;
                    tip = pushPaymentData.getValueOfConvenienceFeeFixed();

                    break;
                case PushPaymentData.TipConvenienceIndicator.PERCENTAGE_CONVENIENCE_FEE:
                    tipInfo = PaymentData.TipInfo.PERCENTAGE_CONVENIENCE_FEE;
                    tip = pushPaymentData.getValueOfConvenienceFeePercentage();

                    break;
                case PushPaymentData.TipConvenienceIndicator.PROMTED_TO_ENTER_TIP:
                    tipInfo = PaymentData.TipInfo.PROMPTED_TO_ENTER_TIP;
                    tip = 0;

                    break;
            }
        }

        Long userId = user.getId();
        PaymentInstrument selectedPaymentInstrument = user.getPaymentInstruments().get(selectedCardIdx);

        return new PaymentData(userId, selectedPaymentInstrument.getId(), pushPaymentData.isDynamic(), pushPaymentData.getTransactionAmount(), tipInfo, tip, pushPaymentData.getTransactionCurrencyCode(), merchant(pushPaymentData));
    }

    private Merchant merchant(PushPaymentData pushPaymentData) {
        Merchant merchant = new Merchant();

        merchant.setName(pushPaymentData.getMerchantName());
        merchant.setCity(pushPaymentData.getMerchantCity());
        merchant.setCategoryCode(pushPaymentData.getMerchantCategoryCode());
        merchant.setIdentifierVisa02(pushPaymentData.getMerchantIdentifierVisa02());
        merchant.setIdentifierVisa03(pushPaymentData.getMerchantIdentifierVisa03());
        merchant.setIdentifierMastercard04(pushPaymentData.getMerchantIdentifierMastercard04());
        merchant.setIdentifierMastercard05(pushPaymentData.getMerchantIdentifierMastercard05());
        merchant.setIdentifierNPCI06(pushPaymentData.getMerchantIdentifierNPCI06());
        merchant.setIdentifierNPCI07(pushPaymentData.getMerchantIdentifierNPCI07());

        return merchant;
    }

    public void showLogoutProgress() {
        hideLogoutProgress();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.logging_out));
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    public void hideLogoutProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void logout() {
        showLogoutProgress();

        ServiceGenerator.getInstance().mpqrPaymentService().logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                hideLogoutProgress();

                LoginManager.getInstance().logout();

                showLoginActivity();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLogoutProgress();

                if (!call.isCanceled()) {
                    logoutFailed();
                }
            }
        });
    }

    private void logoutFailed() {
        DialogUtils.showErrorDialog(this, 0, R.string.logout_failed);
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}
