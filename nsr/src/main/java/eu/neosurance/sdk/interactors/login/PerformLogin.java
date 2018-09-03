package eu.neosurance.sdk.interactors.login;

import org.json.JSONException;
import org.json.JSONObject;

import eu.neosurance.sdk.webview.ActivityWebViewManager;
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
            activityWebViewManager.showUrl(url, buildParams());
        } catch (Exception e) {
//            Log.e(TAG, "loginExecuted", e);
        }
    }

    public JSONObject buildParams() throws JSONException {
        JSONObject params = new JSONObject();
        params.put("loginExecuted", "yes");

        return params;
    }
}
