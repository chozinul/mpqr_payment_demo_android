package com.mastercard.labs.mpqrpayment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.PaymentInstrument;
import com.mastercard.labs.mpqrpayment.utils.CurrencyCode;
import com.mastercard.labs.mpqrpayment.utils.DrawableUtils;

import java.util.List;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class CardsArrayAdapter extends ArrayAdapter<PaymentInstrument> {
    private int selectedIndex;

    public CardsArrayAdapter(Context context, List<PaymentInstrument> paymentInstruments) {
        super(context, 0, paymentInstruments);
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

        PaymentInstrument paymentInstrument = getItem(position);
        if (paymentInstrument == null) {
            return view;
        }

        cardLogo.setImageResource(DrawableUtils.getPaymentMethodLogo(paymentInstrument));
        cardNumber.setText(paymentInstrument.getMaskedIdentifier());
        String balanceText = paymentInstrument.getFormattedAmount();
        balance.setText(getContext().getString(R.string.balance_of_card, balanceText));

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
