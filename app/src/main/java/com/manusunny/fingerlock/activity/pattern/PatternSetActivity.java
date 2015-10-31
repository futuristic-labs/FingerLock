package com.manusunny.fingerlock.activity.pattern;

import android.content.SharedPreferences;

import java.util.List;

import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;

import static com.manusunny.fingerlock.service.CurrentStateService.sharedPreferences;

public class PatternSetActivity extends me.zhanghai.patternlock.SetPatternActivity {
    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        String patternSha1 = PatternUtils.patternToSha1String(pattern);
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("pattern_value", patternSha1);
        edit.commit();
    }
}
