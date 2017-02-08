package com.mastercard.labs.mpqrpayment.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.fragment.CardFragment;
import com.mastercard.labs.mpqrpayment.utils.DrawableUtils;

import java.util.List;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/24/17
 */
public class CardPagerAdapter extends FragmentPagerAdapter {
    private List<PaymentInstrument> paymentInstruments;

    public CardPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public List<PaymentInstrument> getPaymentInstruments() {
        return paymentInstruments;
    }

    public void setPaymentInstruments(List<PaymentInstrument> paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        PaymentInstrument paymentInstrument = paymentInstruments.get(position);

        int drawableId = DrawableUtils.getPaymentMethodImage(paymentInstrument);

        return CardFragment.newInstance(drawableId);
    }

    @Override
    public int getCount() {
        return paymentInstruments == null ? 0 : paymentInstruments.size();
    }
}