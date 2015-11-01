package com.manusunny.fingerlock.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.manusunny.fingerlock.activity.lock.PatternConfirmActivity;
import com.manusunny.fingerlock.activity.lock.PatternSetActivity;
import com.manusunny.fingerlock.activity.lock.PinActivity;
import com.manusunny.fingerlock.model.Constants;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class SettingsActivityResultHandler implements Constants {
    SettingsActivity activity;

    public SettingsActivityResultHandler(SettingsActivity activity) {
        this.activity = activity;
    }

    public void pinChange(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PIN_CHANGE && resultCode == RESULT_CODE_FORGOT) {
            final Intent intent = new Intent(activity, PatternConfirmActivity.class);
            intent.putExtra("hideForgot", "true");
            activity.startActivityForResult(intent, REQUEST_CODE_FORGOT_PIN);
        }
        if (requestCode == REQUEST_CODE_FORGOT_PIN && resultCode == Activity.RESULT_OK) {
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("pin_value", "");
            edit.commit();
            Toast.makeText(activity, "Reset successful", Toast.LENGTH_SHORT).show();
        }
    }

    public void patternChange(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PATTERN_CHANGE && resultCode == Activity.RESULT_OK) {
            activity.startActivityForResult(new Intent(activity, PatternSetActivity.class), REQUEST_CODE_PATTERN_SET);
        }
        if (requestCode == REQUEST_CODE_PATTERN_CHANGE && resultCode == RESULT_CODE_FORGOT) {
            final Intent intent = new Intent(activity, PinActivity.class);
            intent.putExtra("hideForgot", "true");
            activity.startActivityForResult(intent, REQUEST_CODE_FORGOT_PATTERN);
        }
        if (requestCode == REQUEST_CODE_FORGOT_PATTERN && resultCode == Activity.RESULT_OK) {
            final String pinValue = data.getExtras().getString("pin", "");
            if (pinValue.equals(sharedPreferences.getString("pin_value", ""))) {
                final SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("pattern_value", "");
                edit.commit();
                Toast.makeText(activity, "Reset successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Invalid PIN", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
