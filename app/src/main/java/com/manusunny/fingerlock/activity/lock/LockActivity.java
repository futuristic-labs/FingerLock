package com.manusunny.fingerlock.activity.lock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.manusunny.fingerlock.model.App;
import com.manusunny.fingerlock.model.Constants;
import com.manusunny.fingerlock.service.AppLockService;
import com.manusunny.fingerlock.service.AppService;

import java.util.ArrayList;

public class LockActivity extends Activity implements Constants {

    private String aPackage;
    private String aName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aPackage = getIntent().getExtras().getString("package", "");
        aName = getIntent().getExtras().getString("name", "");
        final ArrayList<App> allApps = new AppService(this).getAllApps();
        App lockedApp = null;
        for (App app : allApps){
            if(app.getPackageName().equals(aPackage)){
                lockedApp = app;
                break;
            }
        }
        switch (lockedApp.getLockMethod()){
            case "0" : {
                final Intent intent = new Intent(this, PinActivity.class);
                intent.putExtra("packageName", aPackage);
                intent.putExtra("appName", aName);
                intent.putExtra("type", "confirm");
                startActivityForResult(intent, 0);
                break;
            }
            case "1" : {
                final Intent intent = new Intent(this, PatternConfirmActivity.class);
                intent.putExtra("packageName", aPackage);
                intent.putExtra("appName", aName);
                startActivityForResult(intent, 1);
                break;
            }
            case "2" : {
                //TODO
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if (resultCode == Activity.RESULT_OK) {
                finish();
            } else if(resultCode == RESULT_CODE_FORGOT) {
                final Intent intent = new Intent(this, PatternConfirmActivity.class);
                intent.putExtra("hideForgot", "true");
                intent.putExtra("packageName", aPackage);
                intent.putExtra("appName", aName);
                startActivityForResult(intent, 1);
            } else {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                AppLockService.lastApp = "";
                finish();
            }
        }
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                finish();
            } else if(resultCode == RESULT_CODE_FORGOT) {
                final Intent intent = new Intent(this, PinActivity.class);
                intent.putExtra("appName", aName);
                intent.putExtra("packageName", aPackage);
                intent.putExtra("type", "confirm");
                intent.putExtra("hideForgot", "true");
                startActivityForResult(intent, 0);
            } else {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                AppLockService.lastApp = "";
                finish();
            }
        }

    }
}
