package com.manusunny.fingerlock.model;

public interface Constants {
    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "fingerlock.db";
    String TABLE_NAME = "apps";
    String COLUMN_ID = "_id";
    String COLUMN_PACKAGE = "package";
    String COLUMN_LOCK_METHOD = "lockMethod";

    String CREATE_TABLE = "create table " + TABLE_NAME + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_PACKAGE + " text not null, " +
            COLUMN_LOCK_METHOD + " text not null);";
    String[] allColumns = {
            COLUMN_ID,
            COLUMN_PACKAGE,
            COLUMN_LOCK_METHOD
    };

    int REQUEST_CODE_PATTERN_SET = 1;
    int REQUEST_CODE_PATTERN_CHANGE = 2;
    int REQUEST_CODE_PIN_SET = 3;
    int REQUEST_CODE_PIN_CHANGE = 4;
    int REQUEST_CODE_FORGOT_PATTERN = 5;
    int REQUEST_CODE_FORGOT_PIN = 6;

    int RESULT_CODE_FORGOT = 1;
    int RESULT_CODE_INVALID_PIN = 2;
}