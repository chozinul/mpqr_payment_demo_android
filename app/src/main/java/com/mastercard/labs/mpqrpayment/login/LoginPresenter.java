package com.mastercard.labs.mpqrpayment.login;

import com.mastercard.labs.mpqrpayment.MainApplication;
import com.mastercard.labs.mpqrpayment.data.DataSource;
import com.mastercard.labs.mpqrpayment.data.model.Card;
import com.mastercard.labs.mpqrpayment.data.model.MethodType;
import com.mastercard.labs.mpqrpayment.data.model.User;
import com.mastercard.labs.mpqrpayment.network.ServiceGenerator;
import com.mastercard.labs.mpqrpayment.network.request.LoginAccessCodeRequest;
import com.mastercard.labs.mpqrpayment.network.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/7/17
 */
public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private DataSource dataSource;
    private Call<LoginResponse> authRequest;

    public LoginPresenter(LoginContract.View view, DataSource dataSource) {
        this.mView = view;
        this.dataSource = dataSource;
    }

    @Override
    public void start() {

    }

    private boolean isPinValid(String pin) {
        return pin != null && pin.length() == 6;
    }

    @Override
    public void login(String accessCode, String pin) {
        if (authRequest != null) {
            return;
        }

        mView.clearAccessCodeError();
        mView.clearPinError();

        // Check for a valid pin, if the user entered one.
        if (!isPinValid(pin)) {
            mView.setInvalidPinError();
            return;
        }

        // Check for a valid access codes.
        if (accessCode == null || accessCode.isEmpty()) {
            mView.setAccessCodeRequired();
            return;
        }

        mView.showProgress();

        authRequest = ServiceGenerator.getInstance().mpqrPaymentService().login(new LoginAccessCodeRequest(accessCode, pin));
        authRequest.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    loginSuccess(response.body());
                } else {
                    mView.setIncorrectPin();
                }

                mView.hideProgress();

                mView.startMainFlow();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mView.hideProgress();

                mView.showNetworkError();
            }
        });
    }

    private void loginSuccess(LoginResponse response) {
        User user = new User();
        user.setUserId(response.getId());
        user.setFirstName(response.getFirstName());
        user.setLastName(response.getLastName());

        user = dataSource.saveUser(user);

        if (response.getPaymentInstruments() != null) {
            for (LoginResponse.PaymentInstrument instrument : response.getPaymentInstruments()) {
                Card card = new Card();
                card.setCardId(instrument.getId());
                card.setAcquirerName(instrument.getAcquirerName());
                card.setIssuerName(instrument.getIssuerName());
                card.setName(instrument.getName());
                card.setMethodType(instrument.getMethodType());
                card.setBalance(instrument.getBalance());
                card.setMaskedIdentifier(instrument.getMaskedIdentifier());
                card.setCurrencyNumericCode(instrument.getCurrencyNumericCode());

                card = dataSource.saveCard(card);
                if (user.getDefaultCard() == null) {
                    user.setDefaultCard(card);
                }

                user.getCards().add(card);
            }
        }

        user = dataSource.saveUser(user);

        MainApplication.setLoggedInUserId(user.getUserId());
    }
}