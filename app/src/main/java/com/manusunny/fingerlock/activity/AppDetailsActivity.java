package com.manusunny.fingerlock.activity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.manusunny.fingerlock.R;

import java.util.ArrayList;

import static com.manusunny.fingerlock.service.CurrentStateService.appListingUtility;

public class AppDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ApplicationInfo info = null;
        final String packageName = getIntent().getExtras().getString("package");
        final String type = getIntent().getExtras().getString("type");
        final ArrayList<ApplicationInfo> appInfos = ("locked".equals(type)) ?
                appListingUtility.lockedAppInfos : appListingUtility.installedAppInfos;

        for (ApplicationInfo applicationInfo : appInfos) {
            if (applicationInfo.packageName.equals(packageName)) {
                info = applicationInfo;
                break;
            }
        }

        final CharSequence appName = getPackageManager().getApplicationLabel(info);
        final CharSequence appPackage = info.packageName;
        TextView appNameText = (TextView) findViewById(R.id.appName);
        TextView appPackageText = (TextView) findViewById(R.id.appPackage);
        appNameText.setText(appName);
        appPackageText.setText(appPackage);

        final Drawable appIcon = getPackageManager().getApplicationIcon(info);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(appIcon);

    }
}
