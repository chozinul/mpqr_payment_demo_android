package com.mastercard.labs.mpqrpayment.merchant;

import com.mastercard.labs.mpqrpayment.data.model.Merchant;
import com.mastercard.labs.mpqrpayment.data.model.PaymentData;
import com.mastercard.labs.mpqrpayment.network.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/8/17
 */
public class MerchantPresenter implements MerchantContract.Presenter {
    private static final int MERCHANT_CODE_LENGTH = 8;
    private MerchantContract.View mView;

    private final long userId;
    private final long cardId;
    private final String currencyNumericCode;

    private Merchant merchant;
    private Call<Merchant> merchantRequest;

    public MerchantPresenter(long userId, long cardId, String currencyNumericCode, MerchantContract.View view) {
        this.userId = userId;
        this.cardId = cardId;
        this.currencyNumericCode = currencyNumericCode;
        this.mView = view;
    }

    @Override
    public void start() {
        showMerchant();
    }

    @Override
    public void moveToNextStep() {
        if (merchant == null) {
            mView.showInvalidMerchantError();
            return;
        }

        PaymentData paymentData = new PaymentData(userId, cardId, false, null, null, null, currencyNumericCode, merchant);
        mView.showPaymentActivity(paymentData);
    }

    private void showMerchant() {
        if (merchant == null) {
            mView.hideMerchantInfo();
        } else {
            mView.showMerchantInfo(merchant.getName(), merchant.getCity());
        }
    }

    @Override
    public void updateMerchantCode(final String merchantCode) {
        if (merchantRequest != null) {
            merchantRequest.cancel();
        }

        if (!isValidCode(merchantCode)) {
            merchant = null;
            mView.hideMerchantInfo();

            return;
        }

        mView.showInfoProgress();

        merchantRequest = ServiceGenerator.getInstance().mpqrPaymentService().merchant(merchantCode);
        merchantRequest.enqueue(new Callback<Merchant>() {
            @Override
            public void onResponse(Call<Merchant> call, Response<Merchant> response) {
                merchantRequest = null;
                mView.hideInfoProgress();

                if (!response.isSuccessful()) {
                    merchant = null;

                    mView.hideMerchantInfo();
                    mView.showInvalidMerchantError();
                } else {
                    merchant = response.body();
                    showMerchant();
                }

            }

            @Override
            public void onFailure(Call<Merchant> call, Throwable t) {
                merchantRequest = null;

                if (!call.isCanceled()) {
                    merchant = null;
                    mView.hideMerchantInfo();
                    mView.showNetworkError();
                }

                mView.hideInfoProgress();
            }
        });
    }

    @Override
    public void explainMerchantCode() {
        mView.explainMerchantCode();
    }

    private boolean isValidCode(String code) {
        return code != null && code.length() == MERCHANT_CODE_LENGTH;
    }
}
