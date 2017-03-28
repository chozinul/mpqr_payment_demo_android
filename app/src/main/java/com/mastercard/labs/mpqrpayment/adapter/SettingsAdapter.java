package com.mastercard.labs.mpqrpayment.adapter;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.labs.mpqrpayment.data.model.Settings;
import com.mastercard.labs.mpqrpayment.utils.SwipeDetector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.list;


/**
 * Created by kaile on 22/3/17.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder>{
    private SettingsItemListener mListener;

    static Context mcontext;
    List<Settings> settings;

    public SettingsAdapter(List<Settings>settings, SettingsItemListener listener) {
        this.settings = settings;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
        mcontext = parent.getContext();
        return new ViewHolder(view, new ViewHolder.ViewHolderClickListener() {

            @Override
            public void onClick(int position) {
                if (mListener != null) {
                    mListener.onSettingsItemClicked(settings.get(position));
                }
            }

            @Override
            public void onSwipe() {
                view.findViewById(R.id.delete_btn).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLeftSwipe() {
                view.findViewById(R.id.delete_btn).setVisibility(View.GONE);
            }

            @Override
            public void onDelete(int position) {

                mListener.onSettingsItemDelete(settings.get(position));
            }

        });


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Settings settings = this.settings.get(position);


        holder.mTitleTextView.setText(settings.getName());
        holder.mValueTextView.setText(settings.getCard());
        holder.mIdTextView.setText(settings.getId());

    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_title)
        TextView mTitleTextView;

        @BindView(R.id.txt_value)
        TextView mValueTextView;

        @BindView(R.id.txt_id)
        TextView mIdTextView;

        @BindView(R.id.img_editable)
        ImageView mEditableImageView;



        private ViewHolderClickListener mListener;
        private SwipeDetector swipeDetector = new SwipeDetector(){

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downX = event.getX();
                        mSwipeDetected = Action.None;
                        return false; // allow other events like Click to be processed
                    }
                    case MotionEvent.ACTION_MOVE: {
                        upX = event.getX();

                        float deltaX = downX - upX;

                        // horizontal swipe detection
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (deltaX > 0) {
                                mListener.onSwipe();
                                mSwipeDetected = Action.RL;
                                return true;
                            }
                            if (deltaX < 0) {
                                mListener.onLeftSwipe();
                                mSwipeDetected = Action.LR;

                            }
                        }
                    }
                }
                return false;
            }
            };


        ViewHolder(View itemView, ViewHolderClickListener listener) {
            super(itemView);

            mListener = listener;

            ButterKnife.bind(this, itemView);

            itemView.setOnTouchListener(swipeDetector);
            itemView.setOnClickListener(this);

            ImageButton deleteImageView = (ImageButton) itemView.findViewById(R.id.delete_btn);
            deleteImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.onDelete(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (swipeDetector.swipeDetected()) {
                if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                    mListener.onSwipe();
                }
            }
            else
            {
                if (mListener != null) {
                    mListener.onClick(getAdapterPosition());
                }
            }
        }


        interface ViewHolderClickListener {
            void onClick(int position);
            void onSwipe();
            void onLeftSwipe();
            void onDelete(int position);
        }
    }

    public interface SettingsItemListener{
        void onSettingsItemClicked(Settings settings);
        void onSettingsItemDelete(Settings settings);
    }





}


