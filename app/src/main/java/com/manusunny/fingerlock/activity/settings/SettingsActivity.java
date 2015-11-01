package com.manusunny.fingerlock.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.activity.MainActivity;
import com.manusunny.fingerlock.activity.lock.PatternConfirmActivity;
import com.manusunny.fingerlock.activity.lock.PatternSetActivity;
import com.manusunny.fingerlock.activity.lock.PinActivity;
import com.manusunny.fingerlock.model.Constants;
import com.manusunny.fingerlock.service.CurrentStateService;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;


public class SettingsActivity extends PreferenceActivity implements Constants {

    private static SettingsActivity activity;
    private static Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "pin_value": {
                    if (sharedPreferences.getString("pin_value", "").equals("")) {
                        final Intent intent = new Intent(activity, PinActivity.class);
                        intent.putExtra("type", "set");
                        intent.putExtra("hideForgot", "true");
                        activity.startActivityForResult(intent, REQUEST_CODE_PIN_SET);
                    }
                    break;
                }
                case "pin_change": {
                    final Intent intent = new Intent(activity, PinActivity.class);
                    intent.putExtra("type", "change");
                    activity.startActivityForResult(intent, REQUEST_CODE_PIN_CHANGE);
                    break;
                }
                case "pattern_value": {
                    if (sharedPreferences.getString("pattern_value", "").equals("")) {
                        final Intent intent = new Intent(activity, PatternSetActivity.class);
                        activity.startActivityForResult(intent, REQUEST_CODE_PATTERN_SET);
                    }
                    break;
                }
                case "pattern_change": {
                    final Intent intent = new Intent(activity, PatternConfirmActivity.class);
                    intent.putExtra("type", "change");
                    activity.startActivityForResult(intent, REQUEST_CODE_PATTERN_CHANGE);
                    break;
                }
            }
            return false;
        }
    };
    private SettingsActivityResultHandler handler;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = SettingsActivity.this;
        handler = new SettingsActivityResultHandler(activity);
        final String pin = sharedPreferences.getString("pin_value", "");
        if (!pin.equals("")) {
            CurrentStateService.pin = pin;
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handler.patternChange(requestCode, resultCode, data);
        handler.pinChange(requestCode, resultCode, data);
        recreate();
    }
}
