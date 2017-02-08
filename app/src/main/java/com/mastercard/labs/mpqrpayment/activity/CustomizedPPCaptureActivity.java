package com.mastercard.labs.mpqrpayment.activity;

import com.google.zxing.client.android.Intents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.mpqr.pushpayment.scan.activity.PPCaptureActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/8/17
 */
public class CustomizedPPCaptureActivity extends PPCaptureActivity {

    public static final int RESULT_CANNOT_SCAN_QR = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected DecoratedBarcodeView initializeContent() {
        this.setContentView(R.layout.activity_pp_capture_customized);
        ButterKnife.bind(this);

        return (DecoratedBarcodeView)this.findViewById(com.mastercard.mpqr.pushpayment.scan.R.id.zxing_barcode_scanner);
    }

    @OnClick(value = R.id.txt_cannot_scan)
    public void cannotScanPressed(View view) {
        Intent intent = new Intent(Intents.Scan.ACTION);
        this.setResult(RESULT_CANNOT_SCAN_QR, intent);
        finish();
    }
}