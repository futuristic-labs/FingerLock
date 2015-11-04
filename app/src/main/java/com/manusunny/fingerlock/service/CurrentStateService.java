package com.manusunny.fingerlock.service;

import android.content.SharedPreferences;

public class CurrentStateService {
    public static SharedPreferences sharedPreferences;
    public static String pin = "";
    public static boolean fingerprintAvailable = false;
}
