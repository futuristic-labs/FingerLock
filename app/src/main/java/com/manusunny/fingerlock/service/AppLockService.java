package com.manusunny.fingerlock.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
                String appName = "";
                ArrayList<App> allApps = appService.getAllApps();
                ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

                if (Build.VERSION.SDK_INT > 20) {
                    appName = mActivityManager.getRunningAppProcesses().get(0).processName;
                } else {
                    appName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                }
                for (App app : allApps) {
                    if (appName.equals(app.getPackageName()) && !lastApp.equals(appName)) {
                        lastApp = appName;
                        final Intent intent = new Intent(AppLockService.this, LockActivity.class);
                        intent.putExtra("package", appName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                if(!appName.equals("com.manusunny.fingerlock")) {
                    lastApp = appName;
                }
            }
        }, 20, 200);

    }

}
