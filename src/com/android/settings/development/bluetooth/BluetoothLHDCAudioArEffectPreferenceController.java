/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.lifecycle.Lifecycle;
import android.util.Log;

/**
 * Switch preference controller for LHDC AR Effect ON/OFF
 */
public class BluetoothLHDCAudioArEffectPreferenceController extends
        AbstractBluetoothA2dpPreferenceController {

    private static final int DEFAULT_INDEX = 0;
    private static final String BLUETOOTH_SELECT_A2DP_LHDC_AR_EFFECT_KEY =
            "bluetooth_enable_a2dp_codec_lhdc_ar_effect";

    private static final int LHDC_FEATURE_MASK = 0xFF000000;
    private static final int LHDC_FEATURE_TAG = 0x4C000000;
    private static final int LHDC_AR_FEATURE = 0x02;

    public BluetoothLHDCAudioArEffectPreferenceController(Context context, Lifecycle lifecycle,
            BluetoothA2dpConfigStore store) {
        super(context, lifecycle, store);
    }

    @Override
    public String getPreferenceKey() {
        return BLUETOOTH_SELECT_A2DP_LHDC_AR_EFFECT_KEY;
    }

    @Override
    protected String[] getListValues() {
        return mContext.getResources().getStringArray(
                R.array.bluetooth_enable_a2dp_codec_lhdc_ar_effect_values);
    }

    @Override
    protected String[] getListSummaries() {
        return mContext.getResources().getStringArray(
                R.array.bluetooth_enable_a2dp_codec_lhdc_ar_effect_summaries);
    }

    @Override
    protected int getDefaultIndex() {
        return DEFAULT_INDEX;
    }

    @Override
    protected void writeConfigurationValues(Object newValue) {
        final int index = mPreference.findIndexOfValue(newValue.toString());
        
        int codecSpecific3Value = 0; // default
        codecSpecific3Value |= LHDC_FEATURE_TAG;
        
        if (index != 0) {
            codecSpecific3Value |= LHDC_AR_FEATURE;
        }else{
	        codecSpecific3Value &= ~LHDC_AR_FEATURE;
        }
        mBluetoothA2dpConfigStore.setCodecSpecific3Value(codecSpecific3Value);
    }
    
     /**
     * To get the current A2DP codec config.
     *
     * @return {@link BluetoothCodecConfig}.
     */
    protected BluetoothCodecConfig getCurrentCodecConfig() {
        final BluetoothA2dp bluetoothA2dp = mBluetoothA2dp;
        if (bluetoothA2dp == null) {
            return null;
        }
        BluetoothDevice activeDevice = bluetoothA2dp.getActiveDevice();
        if (activeDevice == null) {
            Log.d(TAG, "Unable to get current codec config. No active device.");
            return null;
        }
        final BluetoothCodecStatus codecStatus =
                bluetoothA2dp.getCodecStatus(activeDevice);
        if (codecStatus == null) {
            Log.d(TAG, "Unable to get current codec config. Codec status is null");
            return null;
        }
        return codecStatus.getCodecConfig();
    }
    
    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        // Enable preference when current codec type is LHDCV1/V2/V3. For other cases, disable it.
        final BluetoothCodecConfig currentConfig = getCurrentCodecConfig();
        if (currentConfig != null
                && (currentConfig.getCodecType() == BluetoothCodecConfig.SOURCE_CODEC_TYPE_LHDCV1 || 
                    currentConfig.getCodecType() == BluetoothCodecConfig.SOURCE_CODEC_TYPE_LHDCV2 ||
                    currentConfig.getCodecType() == BluetoothCodecConfig.SOURCE_CODEC_TYPE_LHDCV3)
                ) {
            preference.setEnabled(true);
        } else {
            preference.setEnabled(false);
            preference.setSummary("");
        }
    }

    @Override
    protected int getCurrentA2dpSettingIndex(BluetoothCodecConfig config) {
        int ret = 0;
        int index = (int)config.getCodecSpecific3();
        int tmp = index & LHDC_FEATURE_MASK;
        if (tmp == LHDC_FEATURE_TAG) {
            if ((index & LHDC_AR_FEATURE) != 0) {
                ret = 1;
            } else {
                ret = 0;
            }
        } else {
            ret = 0;
        }
        return ret;
    }
}
