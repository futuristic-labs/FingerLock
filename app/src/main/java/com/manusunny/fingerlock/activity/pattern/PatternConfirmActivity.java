package com.manusunny.fingerlock.activity.pattern;

import android.content.Intent;
import android.text.TextUtils;

import com.manusunny.fingerlock.service.CurrentStateService;

import java.util.List;

import me.zhanghai.patternlock.ConfirmPatternActivity;
import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;

public class PatternConfirmActivity extends ConfirmPatternActivity {
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

//        startActivity(new Intent(this, YourResetPatternActivity.class));

        // Finish with RESULT_FORGOT_PASSWORD.
        super.onForgotPassword();
    }


}
