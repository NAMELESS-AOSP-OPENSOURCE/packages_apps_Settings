package com.android.settings.gestures;

import android.content.Context;
import android.provider.Settings;
import android.os.UserHandle;

import com.android.settings.core.BasePreferenceController;

public class InverseNavbarLayoutPreferenceController extends BasePreferenceController {
    
    private static final String PREF_KEY = "sysui_nav_bar_inverse";

    private Context mContext;

    public InverseNavbarLayoutPreferenceController(Context context) {
        super(context, PREF_KEY);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        return Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.NAVIGATION_MODE, 2,
                UserHandle.USER_CURRENT) != 2 ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }
}
