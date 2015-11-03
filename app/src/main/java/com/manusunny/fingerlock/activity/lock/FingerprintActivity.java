package com.manusunny.fingerlock.activity.lock;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.manusunny.fingerlock.R;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

public class FingerprintActivity extends AppCompatActivity {
    private SpassFingerprint mSpassFingerprint;
    private Spass mSpass;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        extras = getIntent().getExtras();
        setupAppName();
        setupAppImage();
        authenticateUsingFingerprint(getListener());
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

    private void initialize() {
        try {
            mSpass = new Spass();
            mSpassFingerprint = new SpassFingerprint(this);
            mSpass.initialize(this);
            System.out.println("Spass initialized");
        } catch (SsdkUnsupportedException | UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateUsingFingerprint(SpassFingerprint.IdentifyListener listener) {
        initialize();
        boolean isFeatureEnabled = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_CUSTOMIZED_DIALOG);
        if (isFeatureEnabled) {
            System.out.println("Spass with dialog available");
            mSpassFingerprint = new SpassFingerprint(this);
        } else {
            System.out.println("Custom Dialog Fingerprint Service is not supported in the device.");
        }
        mSpassFingerprint.setCanceledOnTouchOutside(false);
        mSpassFingerprint.setDialogBgTransparency(0);
        mSpassFingerprint.startIdentify(listener);
        return true;
    }

    private SpassFingerprint.IdentifyListener getListener() {
        return new SpassFingerprint.IdentifyListener() {
            @Override
            public void onFinished(int eventStatus) {
                if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                    System.out.println("Auth success");
                    setResult(Activity.RESULT_OK);
                    finish();
                } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                    System.out.println("Auth success with password");
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(FingerprintActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                    System.out.println("Auth failed");
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }

            @Override
            public void onReady() {
                // It is called when fingerprint identification is ready after
                // startIdentify() is called.
            }

            @Override
            public void onStarted() {
                // It is called when the user touches the fingerprint sensor after
                // startIdentify() is called.
            }
        };
    }

    @Override
    public void onBackPressed() {
        System.out.println("Cancelled");
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
