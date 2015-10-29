package com.manusunny.fingerlock.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.manusunny.fingerlock.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.manusunny.fingerlock.service.CurrentStateService.appService;

public class AppListingUtility {
    public ArrayList<ApplicationInfo> lockedAppInfos;
    public ArrayList<ApplicationInfo> installedAppInfos;
    public boolean wait = true;
    public HashSet<String> lockedAppNames;
    public HashSet<String> installedAppNames;
    private Context context;

    public AppListingUtility(Context context) {
        this.context = context;
        prepareList();
    }

    private void prepareList() {
        installedAppInfos = new ArrayList<>(0);
        installedAppNames = new HashSet<>(0);
        lockedAppInfos = new ArrayList<>(0);
        lockedAppNames = new HashSet<>(0);
        PackageManager packageManager = this.context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfos) {
            installedAppNames.add(resolveInfo.activityInfo.packageName);
        }
        addAppsToDatabase(resInfos);

        processPackageNames();
        prepareAppInfo(packageManager);
        Collections.sort(installedAppInfos, new ApplicationInfo.DisplayNameComparator(packageManager));
        Collections.sort(lockedAppInfos, new ApplicationInfo.DisplayNameComparator(packageManager));
        wait = false;
    }

    private void addAppsToDatabase(List<ResolveInfo> resInfos) {
        appService.addApp("com.manusunny.fingerlock", "false", "false", "false");
        appService.addApp(resInfos.get(1).activityInfo.packageName, "false", "false", "false");
        appService.addApp(resInfos.get(2).activityInfo.packageName, "false", "false", "false");
        appService.addApp(resInfos.get(3).activityInfo.packageName, "false", "false", "false");
    }

    private void processPackageNames() {
        ArrayList<App> allApps = appService.getAllApps();
        for (App app : allApps) {
            if (installedAppNames.contains(app.getPackageName())) {
                installedAppNames.remove(app.getPackageName());
                lockedAppNames.add(app.getPackageName());
            } else {
                appService.removeApp(app.getId());
            }
        }
    }

    private void prepareAppInfo(PackageManager packageManager) {
        for (String packageName : installedAppNames) {
            try {
                installedAppInfos.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        for (String packageName : lockedAppNames) {
            try {
                lockedAppInfos.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
