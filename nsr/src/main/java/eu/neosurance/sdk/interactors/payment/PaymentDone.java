package eu.neosurance.sdk.interactors.payment;

import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.utils.ActivityWebViewManager;
import eu.neosurance.sdk.utils.PrecheckUtils;

public class PaymentDone {
    protected static final String TAG = PaymentDone.class.getCanonicalName();

    private final ActivityWebViewManager activityWebViewManager;

    public PaymentDone(ActivityWebViewManager activityWebViewManager) {
        this.activityWebViewManager = activityWebViewManager;
    }

    public void execute(JSONObject paymentInfo, String url) {
        PrecheckUtils.guaranteeMinimalAndroidVersion();

        try {
            JSONObject params = new JSONObject();
            params.put("paymentExecuted", paymentInfo.toString());
            activityWebViewManager.showUrl(url, params);
        } catch (Exception e) {
            Log.e(TAG, "paymentExecuted", e);
        }
    }
}
