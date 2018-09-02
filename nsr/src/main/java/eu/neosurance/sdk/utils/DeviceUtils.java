package eu.neosurance.sdk.utils;

import android.content.Context;
import android.provider.Settings;

public class DeviceUtils {
    public static String getDeviceUid(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
