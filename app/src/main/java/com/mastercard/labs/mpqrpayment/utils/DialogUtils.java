package com.mastercard.labs.mpqrpayment.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mastercard.labs.mpqrpayment.R;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/2/17
 */
public class DialogUtils {
    public static void showDialog(Context context, @StringRes int title, @StringRes int message) {
        customAlertDialogBuilder(context, message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private static AlertDialog.Builder customAlertDialogBuilder(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),
                R.drawable.alert_title_header, options);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView titleImageView = new ImageView(context);
        titleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        titleImageView.setImageResource(R.drawable.alert_title_header);
        titleImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, options.outHeight));

        View borderView = new View(context);
        borderView.setBackgroundResource(R.drawable.drawable_divider);
        borderView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelSize(R.dimen.divider_height)));

        layout.addView(titleImageView);
        layout.addView(borderView);

        return new AlertDialog.Builder(context)
                .setCustomTitle(layout);
    }

    public static AlertDialog.Builder customAlertDialogBuilder(Context context, @StringRes int message) {
        TextView messageTextView = new TextView(context);
        messageTextView.setText(message);
        messageTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        int padding = context.getResources().getDimensionPixelSize(R.dimen.size_10);
        float textSize = context.getResources().getDimension(R.dimen.size_x);
        messageTextView.setPadding(padding, padding, padding, padding);
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        messageTextView.setTextColor(ContextCompat.getColor(context, R.color.colorTextMainColor));

        return customAlertDialogBuilder(context).setView(messageTextView);
    }
}
