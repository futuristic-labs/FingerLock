package com.manusunny.fingerlock.activity.pin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.model.Constants;

public class PinActivity extends Activity implements Constants {

    private EditText pin1;
    private EditText pin2;
    private EditText pin3;
    private EditText pin4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPINInput();
        setupKeyListeners();
        setupForgotPIN();
    }

    private void setupForgotPIN() {
        TextView forgot = (TextView) findViewById(R.id.forgotPin);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CODE_FORGOT);
                finish();
            }
        });
        final String hideForgot = getIntent().getExtras().getString("hideForgot");
        if ("true".equals(hideForgot)) {
            forgot.setVisibility(View.INVISIBLE);
        }
    }

    private void setupPINInput() {
        setContentView(R.layout.activity_pin);
        String pinText = getIntent().getExtras().getString("pinText", "");
        if (!pinText.equals("")) {
            TextView text = (TextView) findViewById(R.id.pinText);
            text.setText(pinText);
        }

        setupTextBoxes();
        if (pin1.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

    }

    private void setupKeyListeners() {
        final View.OnKeyListener keyListener = getOnKeyListener();
        pin1.setOnKeyListener(keyListener);
        pin2.setOnKeyListener(keyListener);
        pin3.setOnKeyListener(keyListener);
        pin4.setOnKeyListener(keyListener);
    }

    private void setupTextBoxes() {
        pin1 = (EditText) findViewById(R.id.pin1);
        pin2 = (EditText) findViewById(R.id.pin2);
        pin3 = (EditText) findViewById(R.id.pin3);
        pin4 = (EditText) findViewById(R.id.pin4);

        pin1.setText("");
        pin2.setText("");
        pin3.setText("");
        pin4.setText("");

        pin1.addTextChangedListener(getWatcher(pin2));
        pin2.addTextChangedListener(getWatcher(pin3));
        pin3.addTextChangedListener(getWatcher(pin4));
        pin4.addTextChangedListener(getWatcher(null));
    }

    @NonNull
    private View.OnKeyListener getOnKeyListener() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    setupPINInput();
                }
                return false;
            }
        };
    }

    private TextWatcher getWatcher(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText == null && !pin4.getText().toString().equals("")) {
                    Intent resultIntent = new Intent();
                    final String value = "" + pin1.getText() + pin2.getText() + pin3.getText() + pin4.getText();
                    resultIntent.putExtra("pin", value);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else if (editText.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("pin", "");
        final PinActivity activity = PinActivity.this;
        activity.setResult(0, resultIntent);
        activity.finish();
    }
}
