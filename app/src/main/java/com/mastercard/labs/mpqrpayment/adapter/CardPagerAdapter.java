package com.mastercard.labs.mpqrpayment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.fragment.CardFragment;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/24/17
 */
public class CardPagerAdapter extends FragmentPagerAdapter {
    private static int[] covers = {R.drawable.card_1_large, R.drawable.card_2_large};


    public CardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override public Fragment getItem(int position) {
        return CardFragment.newInstance(covers[position]);
    }

    @Override public int getCount() {
        return covers.length;
    }
}