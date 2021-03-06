package com.mastercard.labs.mpqrpayment.activity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.adapter.CardPagerAdapter;
import com.mastercard.labs.mpqrpayment.data.DataSource;
import com.mastercard.labs.mpqrpayment.data.RealmDataSource;
import com.mastercard.labs.mpqrpayment.data.model.Merchant;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.login.LoginActivity;
import com.mastercard.labs.mpqrpayment.network.LoginManager;
import com.mastercard.labs.mpqrpayment.network.ServiceGenerator;
import com.mastercard.labs.mpqrpayment.payment.PaymentActivity;
import com.mastercard.labs.mpqrpayment.utils.DialogUtils;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.AdditionalData;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.scan.PPIntentIntegrator;
import com.mastercard.mpqr.pushpayment.scan.constant.PPIntents;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    public static String BUNDLE_SELECTED_CARD_IDX = "selectedCardIdx";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pager_container)
    PagerContainer pagerContainer;

    @BindView(R.id.txtAvailableBalanceAmount)
    TextView availableBalanceTextView;

    private ProgressDialog progressDialog;

    DataSource dataSource;

    private Long userId;
    private User user;
    private int selectedCardIdx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        userId = LoginManager.getInstance().getLoggedInUserId();

        if (userId == -1) {
            return;
        }

        dataSource = RealmDataSource.getInstance();

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

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    // Page listener methods
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedCardIdx = position;
        updateBalance();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Updates balance of card.
     */
    private void updateBalance() {
        PaymentInstrument selectedPaymentInstrument = user.getPaymentInstruments().get(selectedCardIdx);

        availableBalanceTextView.setText(selectedPaymentInstrument.getFormattedAmount());
    }

    /**
     * Updates cards accordingly to current user.
     */
    private void invalidateViews() {
        user = dataSource.getUser(userId);
        if (user == null) {
            // TODO: Show error
            return;
        }

        refreshCards(user);
    }

    /**
     * Refreshes cards in viewPager.
     */
    private void refreshCards(User user) {
        if (user.getPaymentInstruments().isEmpty()) {
            return;
        }

        //if no card is currently selected. get default card and set as selected
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


        //update viewPager to show newest card list
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

                if (fragment != null && fragment.getView() != null) {
                    ViewCompat.setElevation(fragment.getView(), 8.0f);
                }
            }
        });
    }

    @OnClick(R.id.scan_qr_button)
    public void scanFromCamera() {
        IntentIntegrator integrator = new PPIntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.setPrompt(getString(R.string.txt_scan_qr));
        integrator.setCaptureActivity(CustomizedPPCaptureActivity.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            IntentResult result = PPIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            PushPaymentData pushPaymentData = (PushPaymentData) data.getSerializableExtra(PPIntents.PUSH_PAYMENT_DATA);
            if (pushPaymentData != null) {
                showPaymentActivity(pushPaymentData);
            } else {
                cannotScanQR();
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (data != null) {
                FormatException e = (FormatException) data.getSerializableExtra(PPIntents.PARSE_ERROR);
                if (e != null) {
                    e.printStackTrace();
                    cannotScanQR();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Dialog to show when QR code cannot scan successfully.
     */
    private void cannotScanQR() {
        DialogUtils.customAlertDialogBuilder(this, R.string.error_cannot_scan_qr).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showPaymentActivity(PushPaymentData pushPaymentData) {
        PaymentData paymentData = paymentData(pushPaymentData);

        Intent intent = PaymentActivity.newIntent(this, paymentData);
        startActivity(intent);
    }

    /**
     * Generates PaymentData using data from PushPaymentData instance received.
     * @param pushPaymentData pushpaymentdata to generate from
     * @return paymentData instance
     */
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
        AdditionalData additionalData = pushPaymentData.getAdditionalData();

        boolean isDynamic = false;
        String poi = pushPaymentData.getPointOfInitiationMethod();
        if (poi != null) {
            if (poi.endsWith("2")) {
                isDynamic = true;
            }
        }
        return new PaymentData(userId, selectedPaymentInstrument.getId(), isDynamic, pushPaymentData.getTransactionAmount(), tipInfo, tip, pushPaymentData.getTransactionCurrencyCode(), additionalData == null ? null : additionalData.getMobileNumber(), merchant(pushPaymentData));
    }

    /**
     * Generates Merchant using merchant data from PushPaymentData instance received.
     *
     * @param pushPaymentData pushpaymentdata to generate from
     * @return Merchant instance
     */
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
        if (pushPaymentData.getAdditionalData() != null) {
            merchant.setTerminalNumber(pushPaymentData.getAdditionalData().getTerminalId());
            merchant.setStoreId(pushPaymentData.getAdditionalData().getStoreId());
        }

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
        DialogUtils.customAlertDialogBuilder(this, R.string.logout_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void logoutFailed() {
        DialogUtils.showDialog(this, 0, R.string.logout_failed);
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

}
