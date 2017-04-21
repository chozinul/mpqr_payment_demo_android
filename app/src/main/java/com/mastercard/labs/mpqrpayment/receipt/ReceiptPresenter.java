package com.mastercard.labs.mpqrpayment.receipt;

import com.mastercard.labs.mpqrpayment.data.DataSource;
import com.mastercard.labs.mpqrpayment.data.model.Receipt;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/3/17
 */
public class ReceiptPresenter implements ReceiptContract.Presenter {
    private ReceiptContract.View view;
    private DataSource dataSource;
    private Receipt receipt;

    public ReceiptPresenter(ReceiptContract.View view, DataSource dataSource, Receipt receipt) {
        this.view = view;
        this.dataSource = dataSource;
        this.receipt = receipt;
    }

    @Override
    public void start() {
        view.setTotalAmount(receipt.getTotalAmount(), receipt.getCurrencyCode());
        view.setMerchantName(receipt.getMerchantName());
        view.setMerchantCity(receipt.getMerchantCity());
        view.setAmount(receipt.getAmount());

        if (receipt.getTipAmount() == null) {
            view.hideTipInformation();
        } else {
            view.showTipInformation();
            view.setTipAmount(receipt.getTipAmount());
        }

        view.setMaskedPan(receipt.getMaskedPan());
        view.setMethodType(receipt.getMethodType());
    }
}
