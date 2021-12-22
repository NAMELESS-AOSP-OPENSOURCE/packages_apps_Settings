package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.PreferenceScreen;

import com.android.settings.core.TogglePreferenceController;

import com.android.settings.custom.preference.SystemSettingListPreference;

public class UdfpsBgPreferenceController extends TogglePreferenceController {

    private static final String KEY_UDFPS_COLOR = "udfps_icon_color";

    private SystemSettingListPreference mUdfpsColorPreference;

    public UdfpsBgPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mUdfpsColorPreference = screen.findPreference(KEY_UDFPS_COLOR);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isChecked() {
        return Settings.System.getIntForUser(mContext.getContentResolver(),
                   Settings.System.SHOW_UDFPS_BG, 1, UserHandle.USER_CURRENT) == 1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        Settings.System.putInt(
                mContext.getContentResolver(), Settings.System.SHOW_UDFPS_BG, isChecked ? 1 : 0);
        mUdfpsColorPreference.setEnabled(!isChecked);
        return true;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return 0;
    }
}    
