package com.example.toanpc.barcodecompare.controller;

import com.example.toanpc.barcodecompare.model.ItemCheck;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by toanpc on 09/10/2016.
 */
public class RealmCotroller {
    private static Realm mRealm = Realm.getDefaultInstance();
    public static void saveObject(ItemCheck itemCheck) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(itemCheck);
        mRealm.commitTransaction();
    }
    public static  RealmResults getAll() {
        return mRealm.where(ItemCheck.class).findAll();
    }
}
