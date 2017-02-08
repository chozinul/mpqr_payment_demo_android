package com.mastercard.labs.mpqrpayment.merchant;

import com.mastercard.labs.mpqrpayment.BasePresenter;
import com.mastercard.labs.mpqrpayment.BaseView;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/8/17
 */
public interface MerchantContract {
    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

    }
}
