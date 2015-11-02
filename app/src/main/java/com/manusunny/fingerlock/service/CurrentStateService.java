package com.manusunny.fingerlock.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.manusunny.fingerlock.utilities.AppListingUtility;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;

public class CurrentStateService {
    public static AppListingUtility appListingUtility;
    public static AppService appService;
    public static SharedPreferences sharedPreferences;
    public static String pin = "";
    public static boolean fingerprintAvailable = false;


    public static void prepare(Context context) {
        if (appService == null) {
            appService = new AppService(context);
        }
        if (appListingUtility == null) {
            appListingUtility = new AppListingUtility(context);
        }
        setFingerPrintStatus(context);
    }

    private static void setFingerPrintStatus(Context context) {
        Spass mSpass = new Spass();
        try {
            mSpass.initialize(context);
            fingerprintAvailable = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_CUSTOMIZED_DIALOG);
        } catch (SsdkUnsupportedException e) {
            System.out.println("Fingerprint not supported!");
        }
    }
}
