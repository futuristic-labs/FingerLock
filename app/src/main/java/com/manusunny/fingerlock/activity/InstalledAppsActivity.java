package com.manusunny.fingerlock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.elements.AppListAdapter;

import static com.manusunny.fingerlock.service.CurrentStateService.appListingUtility;

public class InstalledAppsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_apps);
        try {
            getSupportActionBar().setTitle("Installed Apps");
        } catch (Exception e) {
            e.printStackTrace();
        }
        fillData();
    }

    public void fillData() {
        AbsListView mListViewUnlocked = (AbsListView) findViewById(R.id.list_unlocked);
        mListViewUnlocked.setAdapter(new AppListAdapter(this, appListingUtility.installedAppInfos));
        mListViewUnlocked.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
