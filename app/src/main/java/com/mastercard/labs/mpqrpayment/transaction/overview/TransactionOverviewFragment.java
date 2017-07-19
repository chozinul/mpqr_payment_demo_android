package com.mastercard.labs.mpqrpayment.transaction.overview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by kaile on 17/7/17.
 */

public class TransactionOverviewFragment extends Fragment {
    private static final String ARG_CARD_NUMBER = "cardNumber";
    private static final String ARG_ACQUIRER_NAME = "acquirerName";

    private String cardNumber;
    private String acquirerName;

    @BindView(R.id.txt_card_number_value)
    TextView cardNumberTextView;

    @BindView(R.id.txt_acquirer_name_value)
    TextView acquirerNameTextView;

    private Unbinder unbinder;

    public TransactionOverviewFragment() {
        // Required empty public constructor
    }

    public static TransactionOverviewFragment newInstance(String cardNumber, String acquirerName) {
        TransactionOverviewFragment fragment = new TransactionOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CARD_NUMBER, cardNumber);
        args.putString(ARG_ACQUIRER_NAME, acquirerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardNumber = getArguments().getString(ARG_CARD_NUMBER);
            acquirerName = getArguments().getString(ARG_ACQUIRER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_overview, container, false);
        unbinder = ButterKnife.bind(this, view);

        setCardNumber(cardNumber);
        setAcquirerName(acquirerName);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        cardNumberTextView.setText(cardNumber);
    }

    public void setAcquirerName(String acquirerName) {
        this.acquirerName = acquirerName;
        acquirerNameTextView.setText(acquirerName);
    }
}
