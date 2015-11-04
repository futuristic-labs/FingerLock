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
import com.manusunny.fingerlock.service.CurrentStateService;

import java.util.ArrayList;
import java.util.List;

import static com.manusunny.fingerlock.service.CurrentStateService.appListingUtility;
import static com.manusunny.fingerlock.service.CurrentStateService.appService;
import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class AppDetailsActivity extends AppCompatActivity {

    private String packageName;
    private Spinner lockMethodList;
    private Switch lockSwitch;
    private String type;
    private String method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lockMethodList = (Spinner) findViewById(R.id.lock_methods);
        lockSwitch = (Switch) findViewById(R.id.lock_switch);
        packageName = getIntent().getExtras().getString("package");
        type = getIntent().getExtras().getString("type");

        setAppDetails();
        prepareLockSettings();
    }

    private void prepareLockSettings() {
        List<String> methods = new ArrayList<>();
        if (!sharedPreferences.getString("pin_value", "").equals("")) {
            methods.add("PIN");
        }
        if (!sharedPreferences.getString("pattern_value", "").equals("")) {
            methods.add("Pattern");
        }
        if (CurrentStateService.fingerprintAvailable) {
            methods.add("Fingerprint");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lockMethodList.setAdapter(adapter);
        if (type.equals("locked")) {
            method = "App";
            for (App app : appService.getAllApps()) {
                if (app.getPackageName().equals(packageName)) {
                    method = app.getLockMethod();
                    break;
                }
            }
            setSelection();
            lockSwitch.setChecked(type.equals("locked"));
        }
    }

    private void setSelection() {
        for (int i = 0; i < 3; i++) {
            if (lockMethodList.getItemAtPosition(i).equals(method)) {
                lockMethodList.setSelection(i);
                break;
            }
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

        getSupportActionBar().setTitle(getPackageManager().getApplicationLabel(info));
        Drawable appIcon = getPackageManager().getApplicationIcon(info);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(appIcon);
    }

    @Override
    public void onBackPressed() {
        if (type.equals("locked") && !lockSwitch.isChecked()) {
            appService.removeApp(packageName);
            appListingUtility.moveToInstalledList(packageName);
        } else if (type.equals("locked") && !lockMethodList.getSelectedItem().equals(method)) {
            appService.removeApp(packageName);
            appService.addApp(packageName, lockMethodList.getSelectedItem().toString());
        } else if (!type.equals("locked") && lockSwitch.isChecked()) {
            appService.addApp(packageName, lockMethodList.getSelectedItem().toString());
            appListingUtility.moveToLockedList(packageName);
        }
        finish();
    }
}
