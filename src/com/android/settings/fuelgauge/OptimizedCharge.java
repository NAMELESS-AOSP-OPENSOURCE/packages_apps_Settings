/*
 * Copyright (C) 2022 The Nameless-AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.settings.fuelgauge;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;

import androidx.preference.Preference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.custom.preference.SystemSettingSeekBarPreference;

import com.android.settings.preference.SystemSettingMainSwitchPreference;

import com.android.settingslib.widget.OnMainSwitchChangeListener;

public class OptimizedCharge extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, OnMainSwitchChangeListener {

    private SystemSettingSeekBarPreference mCeiling;
    private SystemSettingSeekBarPreference mFloor;

    private SystemSettingMainSwitchPreference mStatus;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.optimized_charge);

        boolean enabled = Settings.System.getInt(getContentResolver(),
                Settings.System.OPTIMIZED_CHARGE_ENABLED, 0) == 1;

        mStatus = (SystemSettingMainSwitchPreference) findPreference("optimized_charge_enabled");
        mStatus.setChecked(enabled);
        mStatus.addOnSwitchChangeListener(this);

        mCeiling = (SystemSettingSeekBarPreference) findPreference("optimized_charge_ceiling");
        mCeiling.setEnabled(enabled);

        mFloor = (SystemSettingSeekBarPreference) findPreference("optimized_charge_floor");
        mFloor.setEnabled(enabled);
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        mStatus.setChecked(isChecked);

        mCeiling.setEnabled(isChecked);
        mFloor.setEnabled(isChecked);
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
