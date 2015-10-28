package com.manusunny.fingerlock.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.elements.AppListAdapter;
import com.manusunny.fingerlock.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.manusunny.fingerlock.activity.MainActivity.appService;

public class AppListActivity extends AppCompatActivity {
    private ArrayList<ApplicationInfo> installedAppInfos;
    private HashSet<String> installedAppNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        try{
            getSupportActionBar().setTitle("Installed Apps");
        } catch (Exception e){
            e.printStackTrace();
        }
        fillData();
    }

    public void fillData() {
        AbsListView mListViewUnlocked = (AbsListView) findViewById(R.id.list_unlocked);
        prepareList();
        mListViewUnlocked.setAdapter(new AppListAdapter(this, installedAppInfos));
        mListViewUnlocked.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void prepareList() {
        installedAppInfos = new ArrayList<>(0);
        installedAppNames = new HashSet<>(0);
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfos) {
            installedAppNames.add(resolveInfo.activityInfo.packageName);
        }
//        appService.addApp("com.manusunny.fingerlock", "false", "false", "false");
//        installedAppNames.remove("com.manusunny.fingerlock");

        processPackageNames();
        prepareAppInfo(packageManager);
        Collections.sort(installedAppInfos, new ApplicationInfo.DisplayNameComparator(packageManager));
    }

    private void processPackageNames() {
        ArrayList<App> allApps = appService.getAllApps();
        for (App app : allApps) {
            if (installedAppNames.contains(app.getPackageName())) {
                installedAppNames.remove(app.getPackageName());
            }
        }
    }

    private void prepareAppInfo(PackageManager packageManager) {
        for (String packageName : installedAppNames) {
            try {
                installedAppInfos.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                //Do Nothing
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
