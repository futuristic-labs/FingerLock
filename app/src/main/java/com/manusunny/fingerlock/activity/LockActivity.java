package com.manusunny.fingerlock.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.manusunny.fingerlock.activity.pattern.PatternConfirmActivity;
import com.manusunny.fingerlock.activity.pin.PinActivity;
import com.manusunny.fingerlock.model.App;
import com.manusunny.fingerlock.model.Constants;
import com.manusunny.fingerlock.service.AppLockService;
import com.manusunny.fingerlock.service.AppService;
import com.manusunny.fingerlock.service.CurrentStateService;

import java.util.ArrayList;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

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
                intent.putExtra("pinText", aName);
                startActivityForResult(intent, 0);
                break;
            }
            case "1" : {
                final Intent intent = new Intent(this, PatternConfirmActivity.class);
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
            final String pinValue = sharedPreferences.getString("pin_value", "");
            if(resultCode == Activity.RESULT_OK && data.getStringExtra("pin").equals(pinValue)){
                finish();
            } else if(resultCode == RESULT_CODE_FORGOT) {
                final Intent intent = new Intent(this, PatternConfirmActivity.class);
                intent.putExtra("hideForgot", "true");
                startActivityForResult(intent, 1);
            } else {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                AppLockService.lastApp = "";
                Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                finish();
            } else if(resultCode == RESULT_CODE_FORGOT) {
                final Intent intent = new Intent(this, PinActivity.class);
                intent.putExtra("pinText", aName);
                intent.putExtra("packageName", aPackage);
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
