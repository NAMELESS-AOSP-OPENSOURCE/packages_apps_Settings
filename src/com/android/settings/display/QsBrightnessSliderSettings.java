package com.android.settings.display;

import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.custom.preference.SystemSettingListPreference;
import com.android.settings.custom.preference.SystemSettingSwitchPreference;

public class QsBrightnessSliderSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_QS_BRIGHTNESS_SLIDER = "qs_show_brightness";
    private static final String KEY_AUTO_BRIGHTNESS_ICON = "qs_show_auto_brightness_button";
    private static final String KEY_SHOW_IN_BOTTOM = "qs_brightness_position_bottom";

    private SystemSettingListPreference mShow;
    private SystemSettingListPreference mAutoBrightnessIcon;
    private SystemSettingSwitchPreference mShowInBottom;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.qs_brightness_slider);

        final boolean show = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.QS_SHOW_BRIGHTNESS, 1, UserHandle.USER_CURRENT) != 0;

        mShow = (SystemSettingListPreference) findPreference(KEY_QS_BRIGHTNESS_SLIDER);
        mShow.setOnPreferenceChangeListener(this);

        mAutoBrightnessIcon = (SystemSettingListPreference) findPreference(KEY_AUTO_BRIGHTNESS_ICON);
        mAutoBrightnessIcon.setEnabled(show);

        mShowInBottom = (SystemSettingSwitchPreference) findPreference(KEY_SHOW_IN_BOTTOM);
        mShowInBottom.setEnabled(show);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShow) {
            boolean show = Integer.parseInt(newValue.toString()) != 0;
            mAutoBrightnessIcon.setEnabled(show);
            mShowInBottom.setEnabled(show);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }
}
