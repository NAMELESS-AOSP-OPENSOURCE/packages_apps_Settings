/*
 * Copyright (C) 2020 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.display;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;

import androidx.preference.Preference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.custom.preference.SystemSettingListPreference;
import com.android.settings.custom.preference.SystemSettingSeekBarPreference;

import com.android.settings.preference.SystemSettingMainSwitchPreference;

import com.android.settingslib.widget.OnMainSwitchChangeListener;

public class NetworkTraffic extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, OnMainSwitchChangeListener {

    private SystemSettingListPreference mIndicatorMode;
    private SystemSettingSeekBarPreference mThreshold;
    private SystemSettingSeekBarPreference mInterval;

    private SystemSettingMainSwitchPreference mSwitchBar;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.network_traffic);

        boolean enabled = Settings.System.getInt(getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_STATE, 0) == 1;

        mSwitchBar = (SystemSettingMainSwitchPreference) findPreference("network_traffic_state");
        mSwitchBar.setChecked(enabled);
        mSwitchBar.addOnSwitchChangeListener(this);

        mIndicatorMode = (SystemSettingListPreference) findPreference("network_traffic_mode");
        mIndicatorMode.setEnabled(enabled);

        mThreshold = (SystemSettingSeekBarPreference) findPreference("network_traffic_autohide_threshold");
        mThreshold.setEnabled(enabled);

        mInterval = (SystemSettingSeekBarPreference) findPreference("network_traffic_refresh_interval");
        mInterval.setEnabled(enabled);
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        mSwitchBar.setChecked(isChecked);

        mIndicatorMode.setEnabled(isChecked);
        mThreshold.setEnabled(isChecked);
        mInterval.setEnabled(isChecked);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }
}
