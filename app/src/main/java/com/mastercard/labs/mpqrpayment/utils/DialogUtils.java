package com.mastercard.labs.mpqrpayment.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class DialogUtils {
    public static void showDialog(Context context, @StringRes int title, @StringRes int message) {
        showDialog(context, title, message, null);
    }

    public static void showDialog(Context context, @StringRes int title, @StringRes int message, final DialogInterface.OnDismissListener dismissListener) {
        TextView messageTextView = new TextView(context);
        messageTextView.setText(message);
        messageTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        int padding = context.getResources().getDimensionPixelSize(R.dimen.size_10);
        float textSize = context.getResources().getDimension(R.dimen.size_x);
        messageTextView.setPadding(padding, padding, padding, padding);
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        messageTextView.setTextColor(ContextCompat.getColor(context, R.color.colorTextMainColor));

        AlertDialog dialog = customAlertDialogBuilder(context)
                .setView(messageTextView)
                .setCancelable(true)
                .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (dismissListener != null) {
                            dismissListener.onDismiss(dialog);
                        }
                    }
                }).create();

        dialog.show();
    }

    private static AlertDialog.Builder customAlertDialogBuilder(Context context) {
        TextView titleEditTextView = new TextView(context);
        titleEditTextView.setBackgroundResource(R.drawable.alert_title_header);

        return new AlertDialog.Builder(context)
                .setCustomTitle(titleEditTextView);
    }
}
