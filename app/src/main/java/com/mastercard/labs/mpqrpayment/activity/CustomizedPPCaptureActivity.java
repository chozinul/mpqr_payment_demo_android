package com.mastercard.labs.mpqrpayment.activity;

import android.os.Bundle;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.mastercard.labs.mpqrpayment.R;
import com.mastercard.mpqr.pushpayment.scan.activity.PPCaptureActivity;

import butterknife.ButterKnife;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/8/17
 */
public class CustomizedPPCaptureActivity extends PPCaptureActivity {

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
}