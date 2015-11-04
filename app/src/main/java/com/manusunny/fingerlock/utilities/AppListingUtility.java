package com.manusunny.fingerlock.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.manusunny.fingerlock.model.App;
import com.manusunny.fingerlock.service.AppService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AppListingUtility {
    public ArrayList<ApplicationInfo> lockedAppInfos;
    public ArrayList<ApplicationInfo> installedAppInfos;
    public boolean wait = true;
    public static AppListingUtility appListingUtility;

    private HashSet<String> temp;
    private Context context;
    private AppService appService;

    private AppListingUtility(Context context) {
        this.context = context;
        temp = new HashSet<>(0);
        appService = AppService.getInstance(context);
        installedAppInfos = new ArrayList<>(0);
        lockedAppInfos = new ArrayList<>(0);
        prepareList();
    }

    public static synchronized AppListingUtility getInstance(Context context) {
        if (appListingUtility == null) {
            appListingUtility = new AppListingUtility(context);
        }
        return appListingUtility;
    }

    private void prepareList() {
        PackageManager packageManager = this.context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfos) {
            temp.add(resolveInfo.activityInfo.packageName);
        }

        try {
            getAppInfos(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Collections.sort(installedAppInfos, new ApplicationInfo.DisplayNameComparator(packageManager));
        Collections.sort(lockedAppInfos, new ApplicationInfo.DisplayNameComparator(packageManager));
        wait = false;
    }

    private void getAppInfos(PackageManager packageManager) throws PackageManager.NameNotFoundException {
        ArrayList<App> allApps = appService.getAllApps();
        for (App app : allApps) {
            if (temp.contains(app.getPackageName())) {
                lockedAppInfos.add(packageManager.getApplicationInfo(app.getPackageName(), PackageManager.GET_META_DATA));
                temp.remove(app.getPackageName());
            } else {
                appService.removeApp(app.getId());
            }
        }
        for (String packageName : temp) {
            installedAppInfos.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        }

    }

    public void moveToLockedList(String packageName) {
        int index = 0;
        for (ApplicationInfo info : installedAppInfos) {
            if (info.packageName.equals(packageName)) {
                index = installedAppInfos.indexOf(info);
                lockedAppInfos.add(info);
                Collections.sort(lockedAppInfos, new ApplicationInfo.DisplayNameComparator(context.getPackageManager()));
            }
        }
        installedAppInfos.remove(index);
    }

    public void moveToInstalledList(String packageName) {
        int index = 0;
        for (ApplicationInfo info : lockedAppInfos) {
            if (info.packageName.equals(packageName)) {
                index = lockedAppInfos.indexOf(info);
                installedAppInfos.add(info);
                Collections.sort(installedAppInfos, new ApplicationInfo.DisplayNameComparator(context.getPackageManager()));
            }
        }
        lockedAppInfos.remove(index);
    }
}