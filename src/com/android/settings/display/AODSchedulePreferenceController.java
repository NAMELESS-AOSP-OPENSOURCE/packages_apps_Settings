package com.android.settings.display;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class AODSchedulePreferenceController extends BasePreferenceController {

    private Context mContext;

    static final int MODE_DISABLED = 0;
    static final int MODE_NIGHT = 1;
    static final int MODE_TIME = 2;
    static final int MODE_MIXED_SUNSET = 3;
    static final int MODE_MIXED_SUNRISE = 4;

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
            default:
            case MODE_DISABLED:
                return mContext.getResources().getString(R.string.disabled);
            case MODE_NIGHT:
                return mContext.getResources().getString(R.string.night_display_auto_mode_twilight);
            case MODE_TIME:
                return mContext.getResources().getString(R.string.night_display_auto_mode_custom);
            case MODE_MIXED_SUNSET:
                return mContext.getResources().getString(R.string.always_on_display_schedule_mixed_sunset);
            case MODE_MIXED_SUNRISE:
                return mContext.getResources().getString(R.string.always_on_display_schedule_mixed_sunrise);
        }
    }
}
