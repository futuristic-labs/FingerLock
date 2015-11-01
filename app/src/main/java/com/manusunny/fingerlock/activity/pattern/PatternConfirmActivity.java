package com.manusunny.fingerlock.activity.pattern;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.manusunny.fingerlock.model.Constants;
import com.manusunny.fingerlock.service.CurrentStateService;

import java.util.List;

import me.zhanghai.patternlock.ConfirmPatternActivity;
import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;

public class PatternConfirmActivity extends ConfirmPatternActivity implements Constants {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        if(extras != null){
            final String hideForget = extras.getString("hideForgot", "");
            if (hideForget.equals("true")) {
                rightButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected boolean isStealthModeEnabled() {
        // TODO: Return the value from SharedPreferences.
        return false;
    }

    @Override
    protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        String patternSha1 = CurrentStateService.sharedPreferences.getString("pattern_value", "");
        return TextUtils.equals(PatternUtils.patternToSha1String(pattern), patternSha1);
    }

    @Override
    protected void onForgotPassword() {
        super.onForgotPassword();
        finish();
    }
}
