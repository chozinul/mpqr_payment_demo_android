package com.mastercard.labs.mpqrpayment.data.model;

/**
 * Created by kaile on 22/3/17.
 */


public class Settings {
    private String merchantName;
    private String merchantCard;
    private String merchantId;
    private int position;
    private boolean isEditable;

    public Settings(String merchant, String card, String id, int position, boolean isEditable) {
        this.merchantName = merchant;
        this.merchantCard = card;
        this.merchantId = id;
        this.position = position;
        this.isEditable = isEditable;
    }

    public String getName() {
        return merchantName;
    }

    public void setName(String title) {
        this.merchantName = title;
    }

    public String getCard() {
        return merchantCard;
    }

    public void setCard(String value) {
        this.merchantCard = value;
    }

    public String getId() {
        return merchantId;
    }

    public void setId(String value) {
        this.merchantId = value;
    }

    public int getIndex() {
        return position;
    }

    public void setIndex(int index) {
        this.position = index;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }
}
