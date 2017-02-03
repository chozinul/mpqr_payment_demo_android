package com.mastercard.labs.mpqrpayment.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.Card;

import java.util.List;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class CardsArrayAdapter extends ArrayAdapter<Card> {
    private int selectedIndex;

    public CardsArrayAdapter(Context context, List<Card> cards) {
        super(context, 0, cards);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_card_selection, parent, false);
        } else {
            view = convertView;
        }

        ImageView cardLogo = (ImageView) view.findViewById(R.id.img_card_logo);
        TextView cardNumber = (TextView) view.findViewById(R.id.txt_payment_card);
        TextView balance = (TextView) view.findViewById(R.id.txt_balance);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.rb_check);

        Card card = getItem(position);
        if (card == null) {
            return view;
        }

        @DrawableRes int imageId = 0;
        switch (card.getCardType()) {
            case MastercardBlack:
            case MastercardGold:
                imageId = R.drawable.mastercard_logo;
                break;
            case SavingsAccount:
                imageId = R.drawable.savings_account_logo;
            default:
                // TODO: Add default card logo
                break;
        }

        cardLogo.setImageResource(imageId);
        cardNumber.setText(card.getMaskedPan());
        balance.setText(getContext().getString(R.string.balance_of_card, card.getBalance()));

        radioButton.setChecked(selectedIndex == position);

        return view;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
