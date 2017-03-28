package com.mastercard.labs.mpqrpayment.utils;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.logging.Logger;

/**
 * Created by kaile on 23/3/17.
 */

    public class SwipeDetector implements View.OnTouchListener {

        public enum Action {
            LR,
             RL, // Right to Left
            None // when no action was detected
        }

         public static final int MIN_DISTANCE = 100;
        public float upX, downX;
        public Action mSwipeDetected = Action.None;

        public boolean swipeDetected() {
            return mSwipeDetected != Action.None;
        }

        public Action getAction() {
            return mSwipeDetected;
        }

    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }
}
