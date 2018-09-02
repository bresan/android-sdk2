package eu.neosurance.sdk.utils;

public class PrecheckUtils {
    public static boolean isInvalidAndroidVersion = android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP;

    public static void guaranteeMinimalAndroidVersion() {
        if (isInvalidAndroidVersion) {
            return;
        }
    }
}
