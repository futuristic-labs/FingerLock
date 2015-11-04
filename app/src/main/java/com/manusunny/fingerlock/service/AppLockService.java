package com.manusunny.fingerlock.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.manusunny.fingerlock.activity.lock.LockActivity;
import com.manusunny.fingerlock.model.App;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class AppLockService extends Service {
    public static String lastApp = "";

    @Override
    public void onCreate() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String appPackage;
                AppService appService = AppService.getInstance(AppLockService.this);
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
        }, 0, 500);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final Intent intent = new Intent(this, AppLockService.class);
        startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
