package com.manusunny.fingerlock.activity;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.activity.pattern.PatternConfirmActivity;
import com.manusunny.fingerlock.activity.pattern.PatternSetActivity;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;


public class SettingsActivity extends PreferenceActivity {

    private static SettingsActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = SettingsActivity.this;
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceClickListener(onPreferenceClickListener);
    }

    private static Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()){
                case "pin_value" : {
                    if(sharedPreferences.getString("pin_value", "").equals("")){
                        //TODO
                    }
                    break;
                }
                case "pin_change" : {
                    //TODO
                    break;
                }
                case "pattern_value" : {
                    if(sharedPreferences.getString("pattern_value", "").equals("")){
                        //TODO
                    }
                    break;
                }
                case "pattern_change" : {
                    //TODO
                    break;
                }
            }
            return false;
        }
    };

    private static void setSummaries() {
        if (!sharedPreferences.getString("pin_value", "").equals("")) {
            activity.findPreference("pin_value").setTitle("PIN Security");
            activity.findPreference("pin_value").setSummary("Activated");
            activity.findPreference("pin_change").setEnabled(true);
        } else {
            activity.findPreference("pin_change").setEnabled(false);
        }
        if (!sharedPreferences.getString("pattern_value", "").equals("")) {
            activity.findPreference("pattern_value").setTitle("Pattern Security");
            activity.findPreference("pattern_value").setSummary("Activated");
            activity.findPreference("pattern_change").setEnabled(true);
        } else {
            activity.findPreference("pattern_change").setEnabled(false);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }
        addPreferencesFromResource(R.xml.pref_general);
        setupSettings();

    }

    private void setupSettings() {
        setSummaries();
        bindPreferenceSummaryToValue(findPreference("pin_value"));
        bindPreferenceSummaryToValue(findPreference("pin_change"));
        bindPreferenceSummaryToValue(findPreference("pattern_value"));
        bindPreferenceSummaryToValue(findPreference("pattern_change"));
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == -1){
            startActivityForResult(new Intent(activity, PatternSetActivity.class), 1);
        }
        setupSettings();
    }
}
