package com.manusunny.fingerlock.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.fragment.LockedAppsFragment;
import com.manusunny.fingerlock.service.AppLockService;
import com.manusunny.fingerlock.service.CurrentStateService;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class MainActivity extends AppCompatActivity {
    private static ProgressDialog dialog;
    private static FragmentManager fragmentManager;
    private static FragmentTransaction fragmentTransaction;

    private static void addList(Context context) {
        CurrentStateService.prepare(context);
        while (CurrentStateService.appListingUtility.wait) ;
        final LockedAppsFragment lockedAppsFragment = new LockedAppsFragment();
        fragmentTransaction.replace(R.id.fragment_space, lockedAppsFragment);
        fragmentTransaction.commit();
        dialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InstalledAppsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Wait");
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                addList(MainActivity.this);
                if (Build.VERSION.SDK_INT > 20) {
                    setPermission();
                } else {
                    startService(new Intent(MainActivity.this, AppLockService.class));
                }
            }
        }).start();
    }

    private void setPermission() {
        final String permission = sharedPreferences.getString("permission", "");
        if (!permission.equals("true")) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, 1);
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("permission", "true");
            edit.commit();
        } else {
            startService(new Intent(this, AppLockService.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            final Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            startService(new Intent(this, AppLockService.class));
        }
    }
}