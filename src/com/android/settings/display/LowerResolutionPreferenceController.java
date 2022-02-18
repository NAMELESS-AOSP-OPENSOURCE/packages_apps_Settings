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

package com.android.settings.display;

import static android.os.UserHandle.USER_SYSTEM;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.IWindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.util.custom.CustomUtils;

import com.android.settings.core.TogglePreferenceController;
import com.android.settings.custom.preference.SwitchPreference;
import com.android.settings.R;

import com.android.settingslib.display.DisplayDensityConfiguration;

import java.util.List;

public class LowerResolutionPreferenceController extends TogglePreferenceController {

    private static final String TAG = "LowerResolution";
    private static final boolean DEBUG = false;

    private Context mContext;
    private ActivityManager mActivityManager;
    private SwitchPreference mPreference;

    private IOverlayManager mOm;
    private IWindowManager mWm;

    private static float scale;
    private static String PKG_FWB;
    private static String PKG_SYSUI;

    private float new_resW = -1;
    private float new_resH = -1;

    public LowerResolutionPreferenceController(Context context, String key) {
        super(context, key);
        mContext = context;
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        mOm = IOverlayManager.Stub.asInterface(ServiceManager.getService(
                Context.OVERLAY_SERVICE));
        mWm = IWindowManager.Stub.asInterface(ServiceManager.checkService(
                Context.WINDOW_SERVICE));

        scale = CustomUtils.getLowerResolutionScale(mContext.getResources(), false);
        PKG_FWB = mContext.getResources().getString(com.android.internal.R.string.config_lower_resolution_pkg_fwb);
        PKG_SYSUI = mContext.getResources().getString(com.android.internal.R.string.config_lower_resolution_pkg_sysui);

        if (scale > 0 && scale < 1f) {
            Point initialSize = new Point();
            try {
                mWm.getInitialDisplaySize(Display.DEFAULT_DISPLAY, initialSize);
            } catch (RemoteException re) {
                if (DEBUG) Log.i(TAG, "RemoteException catched!");
                throw re.rethrowFromSystemServer();
            }
            new_resW = initialSize.x * scale;
            new_resH = initialSize.y * scale;
            if (DEBUG) Log.i(TAG, "target resolution: " + Float.toString(new_resW) + "x" + Float.toString(new_resH));
        }
    }

    @Override
    public int getAvailabilityStatus() {
        return scale > 0 && scale < 1f &&
                !TextUtils.isEmpty(PKG_FWB) && !TextUtils.isEmpty(PKG_SYSUI) &&
                isAvailableApp(PKG_FWB, mContext) && isAvailableApp(PKG_SYSUI, mContext) &&
                new_resW > 0 && new_resH > 0 ?
                AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isChecked() {
        return SystemProperties.getInt("persist.sys.display.lower_resolution", 0) == 1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        final boolean currChecked = isChecked();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(mContext.getText(R.string.lower_resolution_dlg_title));
        dialog.setMessage(mContext.getText(R.string.lower_resolution_dlg_text));
        dialog.setPositiveButton(
                R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    final float currDensity = (float) mWm.getBaseDisplayDensity(Display.DEFAULT_DISPLAY);
                    if (DEBUG) Log.i(TAG, "currDensity: " + Float.toString(currDensity));
                    if (isChecked) {
                        mWm.setForcedDisplaySize(Display.DEFAULT_DISPLAY, (int) new_resW, (int) new_resH);
                        DisplayDensityConfiguration.setForcedDisplayDensity(Display.DEFAULT_DISPLAY, (int) (currDensity * scale));
                    } else {
                        mWm.clearForcedDisplaySize(Display.DEFAULT_DISPLAY);
                        DisplayDensityConfiguration.setForcedDisplayDensity(Display.DEFAULT_DISPLAY, (int) (currDensity / scale));
                    }
                    mOm.setEnabled(PKG_FWB, isChecked, USER_SYSTEM);
                    mOm.setEnabled(PKG_SYSUI, isChecked, USER_SYSTEM);
                    SystemProperties.set("persist.sys.display.lower_resolution", isChecked ? "1" : "0");
                    killProcesses();
                } catch (RemoteException re) {
                    if (DEBUG) Log.i(TAG, "RemoteException catched!");
                    throw re.rethrowFromSystemServer();
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
        return isChecked() != currChecked;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreference = screen.findPreference(getPreferenceKey());
        refreshSummary(mPreference);
    }

    @Override
    protected void refreshSummary(Preference preference) {
        String summary = mContext.getResources().getString(R.string.lower_resolution_summary,
                mContext.getResources().getString(R.string.config_lower_resolution_string));
        preference.setSummary(summary);
    }

    private void killProcesses() {
        List<RunningAppProcessInfo> processes = mActivityManager.getRunningAppProcesses();

        for (int i = 0; i < processes.size(); i++) {
            if (!processes.get(i).pkgList[0].equals("android") &&
                    !processes.get(i).pkgList[0].contains("com.android.") &&
                    !processes.get(i).pkgList[0].contains("launcher") &&
                    !processes.get(i).pkgList[0].contains("ims")) {
                mActivityManager.killBackgroundProcesses(processes.get(i).pkgList[0]);
            }
        }
    }

    private static boolean isAvailableApp(String packageName, Context context) {
        final PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            int enabled = pm.getApplicationEnabledSetting(packageName);
            return enabled != PackageManager.COMPONENT_ENABLED_STATE_DISABLED &&
                enabled != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return 0;
    }
}
