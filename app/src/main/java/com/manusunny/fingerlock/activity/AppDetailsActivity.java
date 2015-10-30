package com.manusunny.fingerlock.activity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.model.App;

import java.util.ArrayList;

import static com.manusunny.fingerlock.service.CurrentStateService.appListingUtility;
import static com.manusunny.fingerlock.service.CurrentStateService.appService;

public class AppDetailsActivity extends AppCompatActivity {

    private String packageName;
    private Spinner lockMethodList;
    private Switch lockSwitch;
    private String type;
    private int method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        lockMethodList = (Spinner) findViewById(R.id.lock_methods);
        lockSwitch = (Switch) findViewById(R.id.lock_switch);
        packageName = getIntent().getExtras().getString("package");
        type = getIntent().getExtras().getString("type");

        setAppDetails();
        prepareLockSettings();
    }

    private void prepareLockSettings() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lock_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lockMethodList.setAdapter(adapter);
        if (type.equals("locked")) {
            method = 0;
            for (App app : appService.getAllApps()) {
                if (app.getPackageName().equals(packageName)) {
                    method = Integer.parseInt(app.getLockMethod());
                    break;
                }
            }
            lockMethodList.setSelection(method);
            lockSwitch.setChecked(type.equals("locked"));
        }
    }

    private void setAppDetails() {
        ApplicationInfo info = null;
        final ArrayList<ApplicationInfo> appInfos = ("locked".equals(type)) ?
                appListingUtility.lockedAppInfos : appListingUtility.installedAppInfos;
        for (ApplicationInfo applicationInfo : appInfos) {
            if (applicationInfo.packageName.equals(packageName)) {
                info = applicationInfo;
                break;
            }
        }

        TextView appNameText = (TextView) findViewById(R.id.appName);
        TextView appPackageText = (TextView) findViewById(R.id.appPackage);
        appNameText.setText(getPackageManager().getApplicationLabel(info));
        appPackageText.setText(packageName);

        Drawable appIcon = getPackageManager().getApplicationIcon(info);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(appIcon);
    }

    @Override
    public void onBackPressed() {
        if(type.equals("locked") && !lockSwitch.isChecked()){
            appService.removeApp(packageName);
            appListingUtility.moveToInstalledList(packageName);
        } else if(type.equals("locked") && lockMethodList.getSelectedItemPosition() != method) {
            appService.removeApp(packageName);
            appService.addApp(packageName, lockMethodList.getSelectedItemPosition());
        } else if(!type.equals("locked") && lockSwitch.isChecked()) {
            appService.addApp(packageName, lockMethodList.getSelectedItemPosition());
            appListingUtility.moveToLockedList(packageName);
        }
        finish();
    }
}
