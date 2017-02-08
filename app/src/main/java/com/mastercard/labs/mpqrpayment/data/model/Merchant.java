package com.mastercard.labs.mpqrpayment.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com) on 2/8/17
 */
public class Merchant implements Parcelable {
    private String name;
    private String city;
    private String categoryCode;
    private String identifierVisa02;
    private String identifierVisa03;
    private String identifierMastercard04;
    private String identifierMastercard05;
    private String identifierNPCI06;
    private String identifierNPCI07;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getIdentifierVisa02() {
        return identifierVisa02;
    }

    public void setIdentifierVisa02(String identifierVisa02) {
        this.identifierVisa02 = identifierVisa02;
    }

    public String getIdentifierVisa03() {
        return identifierVisa03;
    }

    public void setIdentifierVisa03(String identifierVisa03) {
        this.identifierVisa03 = identifierVisa03;
    }

    public String getIdentifierMastercard04() {
        return identifierMastercard04;
    }

    public void setIdentifierMastercard04(String identifierMastercard04) {
        this.identifierMastercard04 = identifierMastercard04;
    }

    public String getIdentifierMastercard05() {
        return identifierMastercard05;
    }

    public void setIdentifierMastercard05(String identifierMastercard05) {
        this.identifierMastercard05 = identifierMastercard05;
    }

    public String getIdentifierNPCI06() {
        return identifierNPCI06;
    }

    public void setIdentifierNPCI06(String identifierNPCI06) {
        this.identifierNPCI06 = identifierNPCI06;
    }

    public String getIdentifierNPCI07() {
        return identifierNPCI07;
    }

    public void setIdentifierNPCI07(String identifierNPCI07) {
        this.identifierNPCI07 = identifierNPCI07;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.city);
        dest.writeString(this.categoryCode);
        dest.writeString(this.identifierVisa02);
        dest.writeString(this.identifierVisa03);
        dest.writeString(this.identifierMastercard04);
        dest.writeString(this.identifierMastercard05);
        dest.writeString(this.identifierNPCI06);
        dest.writeString(this.identifierNPCI07);
    }

    public Merchant() {
    }

    protected Merchant(Parcel in) {
        this.name = in.readString();
        this.city = in.readString();
        this.categoryCode = in.readString();
        this.identifierVisa02 = in.readString();
        this.identifierVisa03 = in.readString();
        this.identifierMastercard04 = in.readString();
        this.identifierMastercard05 = in.readString();
        this.identifierNPCI06 = in.readString();
        this.identifierNPCI07 = in.readString();
    }

    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel source) {
            return new Merchant(source);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };
}
