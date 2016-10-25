package com.example.toanpc.barcodecompare.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by toannguyen201194 on 04/10/2016.
 */
public class ItemCheck extends RealmObject {
    private Date mTimeCheck;
    private String mBarcode1;
    private String mBarcode2;
    private boolean mStatus;

    public Date getmTimeCheck() {
        return mTimeCheck;
    }

    public void setmTimeCheck(Date mTimeCheck) {
        this.mTimeCheck = mTimeCheck;
    }

    public String getmBarcode2() {
        return mBarcode2;
    }

    public void setmBarcode2(String mBarcode2) {
        this.mBarcode2 = mBarcode2;
    }

    public String getmBarcode1() {
        return mBarcode1;
    }

    public void setmBarcode1(String mBarcode1) {
        this.mBarcode1 = mBarcode1;
    }

    public boolean getmStatus() {
        return mStatus;
    }

    public void setmStatus(boolean mStatus) {
        this.mStatus = mStatus;
    }
}
