/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.settings.development;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.provider.Settings;

import androidx.preference.PreferenceScreen;
import com.android.settings.custom.preference.SwitchPreference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class WifiEnhancedMacRandomizationPreferenceControllerTest {
    private static final String ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG =
            "enhanced_mac_randomization_force_enabled";
    @Mock
    private SwitchPreference mPreference;
    @Mock
    private PreferenceScreen mPreferenceScreen;
    private Context mContext;
    private WifiEnhancedMacRandomizationPreferenceController mController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mContext = RuntimeEnvironment.application;
        mController = new WifiEnhancedMacRandomizationPreferenceController(mContext);
        when(mPreferenceScreen.findPreference(mController.getPreferenceKey()))
                .thenReturn(mPreference);
        mController.displayPreference(mPreferenceScreen);
    }

    @Test
    public void onPreferenceChanged_enabled_shouldTurnOnEnhancedRandomization() {
        mController.onPreferenceChange(mPreference, true /* new value */);

        int mode = Settings.Global.getInt(mContext.getContentResolver(),
                ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG, -1);
        assertThat(mode).isEqualTo(1);
    }

    @Test
    public void onPreferenceChanged_disabled_shouldTurnOffEnhancedRandomization() {
        mController.onPreferenceChange(mPreference, false /* new value */);

        int mode = Settings.Global.getInt(mContext.getContentResolver(),
                ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG, -1);
        assertThat(mode).isEqualTo(0);
    }

    @Test
    public void updateState_preferenceShouldBeChecked() {
        Settings.Global.putInt(mContext.getContentResolver(),
                ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG, 1);
        mController.updateState(mPreference);

        verify(mPreference).setChecked(true);
    }

    @Test
    public void updateState_preferenceShouldNotBeChecked() {
        Settings.Global.putInt(mContext.getContentResolver(),
                ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG, 0);
        mController.updateState(mPreference);

        verify(mPreference).setChecked(false);
    }

    @Test
    public void onDeveloperOptionsDisabled_shouldDisablePreference() {
        mController.onDeveloperOptionsSwitchDisabled();

        int mode = Settings.Global.getInt(mContext.getContentResolver(),
                ENHANCED_MAC_RANDOMIZATION_FEATURE_FLAG, -1);

        assertThat(mode).isEqualTo(0);
        assertThat(mPreference.isChecked()).isFalse();
        assertThat(mPreference.isEnabled()).isFalse();
    }
}
