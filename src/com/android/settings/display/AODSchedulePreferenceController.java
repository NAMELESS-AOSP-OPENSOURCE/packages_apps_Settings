package com.android.settings.display;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class AODSchedulePreferenceController extends BasePreferenceController {

    private Context mContext;

    public AODSchedulePreferenceController(Context context, String key) {
        super(context, key);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        final int mode = Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.DOZE_ALWAYS_ON_AUTO_MODE, 0, UserHandle.USER_CURRENT);
        switch (mode) {
            case 0:
                return mContext.getResources().getString(R.string.disabled);
            case 1:
                return mContext.getResources().getString(R.string.night_display_auto_mode_twilight);
            case 2:
                return mContext.getResources().getString(R.string.night_display_auto_mode_custom);
        }
    }
}
