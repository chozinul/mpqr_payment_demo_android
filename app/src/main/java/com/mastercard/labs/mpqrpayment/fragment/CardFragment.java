package com.mastercard.labs.mpqrpayment.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mastercard.labs.mpqrpayment.R;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 1/24/17
 */
public class CardFragment extends Fragment {
    int resourceId;
    static final String ARG_RES_ID = "ARG_RES_ID";

    public static CardFragment newInstance(int resourceId) {
        CardFragment cardFragment = new CardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_RES_ID, resourceId);
        cardFragment.setArguments(bundle);
        return cardFragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resourceId = getArguments().getInt(ARG_RES_ID);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_card, container, false);
        ImageView coverImageView = (ImageView) rootView.findViewById(R.id.image_cover);
        coverImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), resourceId));
        return rootView;
    }
}
