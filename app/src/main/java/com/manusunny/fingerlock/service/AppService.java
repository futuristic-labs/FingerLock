package com.manusunny.fingerlock.service;

import android.content.ContentValues;
import android.content.Context;

import com.manusunny.fingerlock.model.App;
import com.manusunny.fingerlock.model.Constants;

import java.util.ArrayList;

public class AppService implements Constants {
    private AppDAO appDatabase;

    public AppService(Context context) {
        appDatabase = new AppDAO(context);
    }

    public ArrayList<App> getAllApps() {
        return appDatabase.getAllApps();
    }

    public App addApp(String packageName, int type) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PACKAGE, packageName);
        values.put(COLUMN_LOCK_METHOD, String.valueOf(type));
        return appDatabase.create(values);
    }

    public void removeApp(long id) {
        appDatabase.delete(id);
    }

    public void removeApp(String packageName) {
        for (App app : getAllApps()) {
            if (app.getPackageName().equals(packageName)) {
                removeApp(app.getId());
                return;
            }
        }
    }
}
