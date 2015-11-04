package com.manusunny.fingerlock.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.manusunny.fingerlock.model.App;
import com.manusunny.fingerlock.model.Constants;

import java.util.ArrayList;

public class AppService extends SQLiteOpenHelper implements Constants {
    private SQLiteDatabase database;
    public static AppService appService;

    private AppService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    public static synchronized AppService getInstance(Context context) {
        if (appService == null) {
            appService = new AppService(context);
        }
        return appService;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public boolean isOpen(){
        return database.isOpen();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AppService.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public App addApp(String packageName, String type) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PACKAGE, packageName);
        values.put(COLUMN_LOCK_METHOD, type);
        return create(values);
    }

    public void removeApp(String packageName) {
        for (App app : getAllApps()) {
            if (app.getPackageName().equals(packageName)) {
                delete(app.getId());
                return;
            }
        }
    }

    public void removeApp(int id) {
        delete(id);
    }

    public ArrayList<App> getAllApps() {
        String query = "SELECT " + getAllColumns() + " FROM " + Constants.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<App> apps = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            apps.add(cursorToApp(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return apps;
    }

    private App create(ContentValues values) {
        long appID = database.insert(Constants.TABLE_NAME, null, values);
        Cursor cursor = database.query(Constants.TABLE_NAME,
                allColumns, Constants.COLUMN_ID + " = " + appID, null, null, null, null);
        cursor.moveToFirst();
        App app = cursorToApp(cursor);
        cursor.close();
        System.out.println("App created with ID : " + app.getId());
        return app;
    }

    private void delete(long id) {
        int rowsAffected = database.delete(TABLE_NAME, COLUMN_ID + " = " + id, null);
        if (rowsAffected == 1) {
            System.out.println("App deleted with id: " + id);
        }
    }

    private synchronized App cursorToApp(Cursor cursor) {
        return new App()
                .setId(cursor.getInt(0))
                .setPackageName(cursor.getString(1))
                .setLockMethod(cursor.getString(2));
    }

    private String getAllColumns() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : Constants.allColumns) {
            stringBuilder.append(s);
            stringBuilder.append(",");
        }
        String allColumns = stringBuilder.toString();
        int c = allColumns.lastIndexOf(",");
        return allColumns.substring(0, c);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (database.isOpen()) {
            database.close();
        }
    }
}
