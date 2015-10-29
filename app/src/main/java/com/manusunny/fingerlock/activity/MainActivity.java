package com.manusunny.fingerlock.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.manusunny.fingerlock.service.CurrentStateService;

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
            }
        }).start();
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
