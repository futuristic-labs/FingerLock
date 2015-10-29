package com.manusunny.fingerlock.service;

import android.content.Context;

import com.manusunny.fingerlock.utilities.AppListingUtility;

public class CurrentStateService {
    public static AppListingUtility appListingUtility;
    public static AppService appService;

    public static void prepare(Context context) {
        if (appService == null) {
            appService = new AppService(context);
        }
        if (appListingUtility == null) {
            appListingUtility = new AppListingUtility(context);
        }
    }
}
