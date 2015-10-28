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

public class AppDAO extends SQLiteOpenHelper implements Constants {
    private SQLiteDatabase database;

    public AppDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AppDAO.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public App create(ContentValues values) {
        long appID = database.insert(Constants.TABLE_NAME, null, values);
        Cursor cursor = database.query(Constants.TABLE_NAME,
                allColumns, Constants.COLUMN_ID + " = " + appID, null, null, null, null);
        cursor.moveToFirst();
        App app = cursorToApp(cursor);
        cursor.close();
        return app;
    }

    public App cursorToApp(Cursor cursor) {
        return new App()
                .setId(cursor.getInt(0))
                .setPackageName(cursor.getString(1))
                .setPassLockEnabled(cursor.getString(2))
                .setPatternLockEnabled(cursor.getString(3))
                .setFingerLockEnabled(cursor.getString(4));
    }

    public void edit(long noteId, ContentValues values) {
        System.out.println("Note edited");
        database.update(Constants.TABLE_NAME, values, Constants.COLUMN_ID + " = " + noteId, null);
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

    public void delete(long id) {
        int rowsAffected = database.delete(TABLE_NAME, COLUMN_ID + " = " + id, null);
        if (rowsAffected == 1) {
            System.out.println("App deleted with id: " + id);
        }
    }
}
