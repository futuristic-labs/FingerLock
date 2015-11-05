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
import com.manusunny.fingerlock.utilities.processes.ProcessManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class AppLockService extends Service {
    public static String lastApp = "";
    private static ArrayList<App> allApps;
    private ScheduledThreadPoolExecutor exec;

    @Override
    public void onCreate() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        System.out.println("***** Starting service");
        AppService appService = AppService.getInstance(AppLockService.this);
        allApps = appService.getAllApps();

        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                checkAppsForLock();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private synchronized void checkAppsForLock() {
        HashSet<String> appPackages = getAppPackages();
        boolean found = false;
        if (appPackages.contains("com.manusunny.fingerlock")) {
            return;
        }
        for (App app : allApps) {
            String packageName = app.getPackageName();
            if (appPackages.contains(packageName)) {
                found = true;
                if (!lastApp.equals(packageName)) {
                    lastApp = packageName;
                    System.out.println("Locking " + packageName);
                    startLockActivity(packageName);
                }
                break;
            }
        }
        if (!found) {
            //noinspection LoopStatementThatDoesntLoop
            for (String name : appPackages) {
                lastApp = name;
                break;
            }
        }
    }

    private void startLockActivity(String name) {
        final Intent intent = new Intent(AppLockService.this, LockActivity.class);
        intent.putExtra("package", name);
        ApplicationInfo info = null;
        try {
            info = getPackageManager().getApplicationInfo(name, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        intent.putExtra("name", getPackageManager().getApplicationLabel(info));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private HashSet<String> getAppPackages() {
        HashSet<String> appPackages = new HashSet<>();
        if (Build.VERSION.SDK_INT > 20) {
            return ProcessManager.getRunningForegroundApps(AppLockService.this);
        } else {
            ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            appPackages.add(mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName());
            return appPackages;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final Intent intent = new Intent(this, AppLockService.class);
        exec.shutdown();
        System.out.println("***** ServiceDestroyed");
        startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
