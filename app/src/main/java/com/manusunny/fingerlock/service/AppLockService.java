package com.manusunny.fingerlock.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.manusunny.fingerlock.activity.LockActivity;
import com.manusunny.fingerlock.model.App;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AppLockService extends IntentService {
    public static String lastApp = "";

    public AppLockService() {
        super("AppLockService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timer timer = new Timer();
        final AppService appService = new AppService(this);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String appPackage = "";
                ArrayList<App> allApps = appService.getAllApps();
                ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

                if (Build.VERSION.SDK_INT > 20) {
                    appPackage = mActivityManager.getRunningAppProcesses().get(0).processName;
                } else {
                    appPackage = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                }
                for (App app : allApps) {
                    if (appPackage.equals(app.getPackageName()) && !lastApp.equals(appPackage)) {
                        lastApp = appPackage;
                        final Intent intent = new Intent(AppLockService.this, LockActivity.class);
                        intent.putExtra("package", appPackage);
                        ApplicationInfo info = null;
                        try {
                            info = getPackageManager().getApplicationInfo(appPackage, 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("name", getPackageManager().getApplicationLabel(info));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                if(!appPackage.equals("com.manusunny.fingerlock")) {
                    lastApp = appPackage;
                }
            }
        }, 20, 200);

    }

}
