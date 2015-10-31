package com.manusunny.fingerlock.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.manusunny.fingerlock.activity.pattern.PatternSetActivity;
import com.manusunny.fingerlock.activity.pin.PinActivity;
import com.manusunny.fingerlock.model.Constants;
import com.manusunny.fingerlock.service.CurrentStateService;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class SettingsActivityResultHandler implements Constants {
    SettingsActivity activity;

    public SettingsActivityResultHandler(SettingsActivity activity) {
        this.activity = activity;
    }

    public void pinChange(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PIN_CHANGE && resultCode == Activity.RESULT_OK) {
            final String pin = data.getExtras().getString("pin", "");
            if (sharedPreferences.getString("pin_value", "").equals(pin)) {
                final Intent intent = new Intent(activity, PinActivity.class);
                intent.putExtra("pinText", "Enter new PIN");
                activity.startActivityForResult(intent, REQUEST_CODE_PIN_SET_ONE);
            } else {
                Toast.makeText(activity, "Incorrect PIN!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void pinSetTwoError(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CODE_PIN_SET_TWO && resultCode == 0) {
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("pin_value", CurrentStateService.pin);
            edit.commit();
            final Intent intent = activity.getIntent();
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public void pinSetTwo(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PIN_SET_TWO && resultCode == Activity.RESULT_OK) {
            final String pin = data.getExtras().getString("pin", "");
            if (!sharedPreferences.getString("pin_value", "").equals(pin)) {
                final SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("pin_value", "");
                edit.commit();
                final Intent intent = new Intent(activity, PinActivity.class);
                intent.putExtra("pinText", "PIN mismatch. Try again");
                activity.startActivityForResult(intent, REQUEST_CODE_PIN_SET_ONE);
            } else {
                CurrentStateService.pin = sharedPreferences.getString("pin_value", "");
            }
        }
    }

    public void pinSetOne(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PIN_SET_ONE && resultCode == Activity.RESULT_OK) {
            if (!data.getExtras().getString("pin", "").equals("")) {
                final SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("pin_value", data.getExtras().getString("pin"));
                edit.commit();
                final Intent intent = new Intent(activity, PinActivity.class);
                intent.putExtra("pinText", "Confirm PIN");
                activity.startActivityForResult(intent, REQUEST_CODE_PIN_SET_TWO);
            }
        }
    }

    public void patternChange(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PATTERN_CHANGE && resultCode == Activity.RESULT_OK) {
            activity.startActivityForResult(new Intent(activity, PatternSetActivity.class), REQUEST_CODE_PATTERN_SET);
        }
        if (requestCode == REQUEST_CODE_PATTERN_CHANGE && resultCode == RESULT_CODE_FORGOT_PATTERN) {
            final Intent intent = new Intent(activity, PinActivity.class);
            intent.putExtra("pinText", "Enter PIN");
            intent.putExtra("hideForget", "true");
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
