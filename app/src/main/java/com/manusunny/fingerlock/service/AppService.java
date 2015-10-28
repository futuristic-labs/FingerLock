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

    public App addApp(String packageName, String passLockEnabled, String patternLockEnabled, String fingerLockEnabled) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PACKAGE, packageName);
        values.put(COLUMN_PASS_LOCK_ENABLED, passLockEnabled);
        values.put(COLUMN_PATTERN_LOCK_ENABLED, patternLockEnabled);
        values.put(COLUMN_FINGER_LOCK_ENABLED, fingerLockEnabled);
        return appDatabase.create(values);
    }

    public void removeApp(long id) {
        appDatabase.delete(id);
        System.out.println("App deleted forever");
    }

    public void passLockApp(long noteId) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASS_LOCK_ENABLED, "true");
        cv.put(COLUMN_PATTERN_LOCK_ENABLED, "false");
        cv.put(COLUMN_FINGER_LOCK_ENABLED, "false");
        appDatabase.edit(noteId, cv);
        System.out.println("App locked");
    }

    public void passUnlockApp(long noteId) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASS_LOCK_ENABLED, "false");
        appDatabase.edit(noteId, cv);
        System.out.println("App unlocked");
    }

    public void patternLockApp(long noteId) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASS_LOCK_ENABLED, "false");
        cv.put(COLUMN_PATTERN_LOCK_ENABLED, "true");
        cv.put(COLUMN_FINGER_LOCK_ENABLED, "false");
        appDatabase.edit(noteId, cv);
        System.out.println("App locked");
    }

    public void patternUnlockApp(long noteId) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PATTERN_LOCK_ENABLED, "false");
        appDatabase.edit(noteId, cv);
        System.out.println("App unlocked");
    }

    public void fingerLockApp(long noteId) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASS_LOCK_ENABLED, "false");
        cv.put(COLUMN_PATTERN_LOCK_ENABLED, "false");
        cv.put(COLUMN_FINGER_LOCK_ENABLED, "true");
        appDatabase.edit(noteId, cv);
        System.out.println("App locked");
    }

    public void fingerUnlockApp(long noteId) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FINGER_LOCK_ENABLED, "false");
        appDatabase.edit(noteId, cv);
        System.out.println("App unlocked");
    }
}
