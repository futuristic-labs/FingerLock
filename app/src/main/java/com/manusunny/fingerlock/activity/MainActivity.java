package com.manusunny.fingerlock.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.activity.settings.SettingsActivity;
import com.manusunny.fingerlock.fragment.LockedAppsFragment;
import com.manusunny.fingerlock.model.Constants;
import com.manusunny.fingerlock.service.AppLockService;
import com.manusunny.fingerlock.utilities.AppListingUtility;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;

import static com.manusunny.fingerlock.service.CurrentStateService.fingerprintAvailable;
import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class MainActivity extends AppCompatActivity {
    private static ProgressDialog dialog;
    private static AppListingUtility appListingUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(getFABListener());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("Wait");
        dialog.show();

        setFingerPrintStatus();
        startLockService();
    }

    @NonNull
    private View.OnClickListener getFABListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean pinValue = sharedPreferences.getString("pin_value", "").equals("");
                final boolean patternValue = sharedPreferences.getString("pattern_value", "").equals("");
                if (pinValue && patternValue) {
                    Snackbar.make(view, Constants.NO_SECURITY_METHODS_DEFINED, Snackbar.LENGTH_INDEFINITE)
                            .setAction("ADD", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                    MainActivity.this.startActivity(intent);
                                }
                            }).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, InstalledAppsActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }
        };
    }

    private Thread getDataLoader() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                appListingUtility = AppListingUtility.getInstance(MainActivity.this);
                while (appListingUtility == null || appListingUtility.wait) ;
                final LockedAppsFragment lockedAppsFragment = new LockedAppsFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_space, lockedAppsFragment);
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });
    }

    private void startLockService() {
        final String permission = sharedPreferences.getString("permission", "");
        if (Build.VERSION.SDK_INT > 20 && !permission.equals("true")) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, 1);
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("permission", "true");
            edit.commit();
        } else {
            startService(new Intent(getApplicationContext(), AppLockService.class));
            getDataLoader().start();
        }
    }

    private void setFingerPrintStatus() {
        if (sharedPreferences.getString("isFingerProcessed", "false").equals("true")) {
            return;
        }
        Spass mSpass = new Spass();
        try {
            mSpass.initialize(this);
            fingerprintAvailable = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_CUSTOMIZED_DIALOG);
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("isFingerProcessed", "true");
            edit.commit();
        } catch (SsdkUnsupportedException e) {
            System.out.println("Fingerprint not supported!");
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
            startService(new Intent(getApplicationContext(), AppLockService.class));
            getDataLoader().start();
        }
    }
}
