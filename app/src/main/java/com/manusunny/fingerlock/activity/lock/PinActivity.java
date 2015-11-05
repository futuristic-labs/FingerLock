package com.manusunny.fingerlock.activity.lock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.elements.PinButtonAdaptor;
import com.manusunny.fingerlock.model.Constants;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class PinActivity extends AppCompatActivity implements Constants {

    private String pin;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pin = "";
        extras = getIntent().getExtras();
        setContentView(R.layout.activity_pin);
        getSupportActionBar().hide();
        preparePINInput();
        final String messageText = extras.getString("messageText", "");
        if (!messageText.equals("")) {
            TextView message = (TextView) findViewById(R.id.messageText);
            message.setText(messageText);
        }
        setupButtons();
        setupAppName();
        setupAppImage();
    }

    private void setupAppImage() {
        ImageView image = (ImageView) findViewById(R.id.appImage);
        if (extras != null) {
            final String packageName = extras.getString("packageName", "");
            Drawable applicationIcon = null;
            if (!"".equals(packageName)) {
                try {
                    applicationIcon = getPackageManager().getApplicationIcon(packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                image.setImageDrawable(applicationIcon);
            }
        }
    }

    private void setupAppName() {
        TextView name = (TextView) findViewById(R.id.appName);
        final String appName = extras.getString("appName", "");
        if (!"".equals(appName)) {
            name.setText(appName);
        }
    }

    private void setupButtons() {
        TextView cancel = (TextView) findViewById(R.id.cancelButton);
        TextView forgot = (TextView) findViewById(R.id.forgotPin);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Constants.RESULT_CODE_FORGOT);
                finish();
            }
        });
        final String hideForgot = extras.getString("hideForgot", "");
        if (!hideForgot.equals("") || sharedPreferences.getString("pattern_value", "").equals("")) {
            forgot.setVisibility(View.INVISIBLE);
        }
    }

    private void preparePINInput() {
        GridView pinLayout = (GridView) findViewById(R.id.pinInputGrid);
        pinLayout.setAdapter(new PinButtonAdaptor(this));
    }

    public void handleClick(Button button) {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VIBRATE_KEY_PRESS);
        setupDots();

        if (pin.length() < 3) {
            pin = pin.concat(button.getText().toString());
        } else {
            pin = pin.concat(button.getText().toString());
            processPIN();
        }
    }

    private void setupDots() {
        Button dot;
        switch (pin.length()) {
            case 0: {
                dot = (Button) findViewById(R.id.dot1);
                dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_filled));
                break;
            }
            case 1: {
                dot = (Button) findViewById(R.id.dot2);
                dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_filled));
                break;
            }
            case 2: {
                dot = (Button) findViewById(R.id.dot3);
                dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_filled));
                break;
            }
            case 3: {
                dot = (Button) findViewById(R.id.dot4);
                dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_filled));
                break;
            }
        }
    }

    private void processPIN() {
        final String type = extras.getString("type", "");
        switch (type) {
            case "confirm": {
                if (pin.equals(sharedPreferences.getString("pin_value", ""))) {
                    Intent intent = new Intent();
                    intent.putExtra("pin", pin);
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(VIBRATE_INVALID_PIN);
                    Toast.makeText(this, "Invalid PIN!", Toast.LENGTH_SHORT).show();
                    setResult(Constants.RESULT_CODE_INVALID_PIN);
                }
                finish();
                break;
            }
            case "set": {
                Intent intent = getIntent();
                intent.putExtra("type", "setTwo");
                intent.putExtra("pin", pin);
                intent.putExtra("hideForgot", "true");
                intent.putExtra("messageText", "Confirm PIN");
                startActivity(intent);
                finish();
                break;
            }
            case "setTwo": {
                Intent input = getIntent();
                if (input.getStringExtra("pin").equals(pin)) {
                    Intent intent = new Intent();
                    intent.putExtra("pin", pin);
                    setResult(Activity.RESULT_OK, intent);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("pin_value", pin);
                    edit.commit();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("type", "set");
                    intent.putExtra("hideForgot", "true");
                    intent.putExtra("messageText", "PIN mismatch, Try again");
                    startActivity(intent);
                }
                finish();
                break;
            }
            case "change": {
                if (pin.equals(sharedPreferences.getString("pin_value", ""))) {
                    Intent intent = getIntent();
                    intent.putExtra("type", "set");
                    intent.putExtra("messageText", "Enter new PIN");
                    intent.putExtra("hideForgot", "true");
                    startActivity(intent);
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("type", "change");
                    intent.putExtra("hideForgot", "true");
                    intent.putExtra("messageText", "Invalid PIN, Try again");
                    startActivity(intent);
                }
                finish();
                break;
            }
        }
    }
}
