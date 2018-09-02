package eu.neosurance.sdk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.neosurance.sdk.NSR;
import eu.neosurance.sdk.R;
import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.platform.persistence.PersistenceManager;


public class SetupUtils {

    private static final String TAG = SetupUtils.class.getCanonicalName();

    public static void performSetup(Context context, DataManager dataManager, JSONObject settings,
                                    int requestCodePermissions) {
        PrecheckUtils.guaranteeMinimalAndroidVersion();
        Log.d(TAG, "setup");
        try {
            if (!settings.has("ns_lang")) {
                settings.put("ns_lang", Locale.getDefault().getLanguage());
            }
            if (!settings.has("dev_mode")) {
                settings.put("dev_mode", 0);
            }
            WebView.setWebContentsDebuggingEnabled(settings.getInt("dev_mode") != 0);

            if (!settings.has("push_icon")) {
                settings.put("push_icon", R.drawable.nsr_logo);
            }
            Log.d(TAG, "setup: " + settings);

            // TODO Settings Repository
            dataManager.getSettingsRepository().setSettings(settings);

            if (dataManager.getPersistenceManager().retrieveData("permission_requested") == null &&
                    settings.has("ask_permission") &&
                    settings.getInt("ask_permission") == 1) {

                dataManager.getPersistenceManager().storeData("permission_requested", "*");

                List<String> permissionsList = new ArrayList<String>();

                if (!PermissionUtils.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }

                if (!PermissionUtils.hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }

                if (permissionsList.size() > 0) {
                    ActivityCompat.requestPermissions((Activity) context,
                            permissionsList.toArray(new String[permissionsList.size()]),
                            requestCodePermissions);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setup", e);
        }
    }
}
