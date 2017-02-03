package com.mastercard.labs.mpqrpayment.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.Card;
import com.mastercard.labs.mpqrpayment.fragment.CardFragment;

import java.util.List;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/24/17
 */
public class CardPagerAdapter extends FragmentPagerAdapter {
    private List<Card> cards;

    public CardPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        Card card = cards.get(position);

        int drawableId;
        switch (card.getCardType()) {
            case MastercardBlack:
                drawableId = R.drawable.mastercard_black;
                break;
            case MastercardGold:
                drawableId = R.drawable.mastercard_gold;
                break;
            case SavingsAccount:
                drawableId = R.drawable.saving_account;
                break;
            default:
                drawableId = -1;
                break;
        }

        return CardFragment.newInstance(drawableId);
    }

    @Override
    public int getCount() {
        return cards == null ? 0 : cards.size();
    }
}