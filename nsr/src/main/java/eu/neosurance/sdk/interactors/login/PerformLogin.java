package eu.neosurance.sdk.interactors.login;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.utils.ActivityWebViewManager;
import eu.neosurance.sdk.utils.PrecheckUtils;

public class PerformLogin {
    private ActivityWebViewManager activityWebViewManager;
    protected static final String TAG = PerformLogin.class.getCanonicalName();

    public PerformLogin(ActivityWebViewManager activityWebViewManager) {
        this.activityWebViewManager = activityWebViewManager;
    }

    public void execute(String url) {
        PrecheckUtils.guaranteeMinimalAndroidVersion();
        try {
            JSONObject params = new JSONObject();
            params.put("loginExecuted", "yes");
            activityWebViewManager.showUrl(url, params);
        } catch (Exception e) {
            Log.e(TAG, "loginExecuted", e);
        }
    }
}
