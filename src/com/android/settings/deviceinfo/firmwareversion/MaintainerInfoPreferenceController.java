package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;

import com.android.settings.R;

public class MaintainerInfoPreferenceController extends BasePreferenceController {

    private final String maintainerName;
    private final String maintainerLink;

    private final PackageManager mPackageManager;

    private Uri INTENT_URI_DATA;

    public MaintainerInfoPreferenceController(Context context, String key) {
        super(context, key);

        maintainerName = context.getResources().getString(R.string.config_maintainer_name);
        maintainerLink = context.getResources().getString(R.string.config_maintainer_link);

        mPackageManager = mContext.getPackageManager();
        if (!TextUtils.isEmpty(maintainerLink)) {
            INTENT_URI_DATA = Uri.parse(maintainerLink);
        }
    }

    public int getAvailabilityStatus() {
        return (!TextUtils.isEmpty(maintainerName) && !TextUtils.isEmpty(maintainerLink)) ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public CharSequence getSummary() {
        return maintainerName;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }

        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(INTENT_URI_DATA);
        if (mPackageManager.queryIntentActivities(intent, 0).isEmpty()) {
            return true;
        }

        mContext.startActivity(intent);
        return true;
    }
}
