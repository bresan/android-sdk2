package eu.neosurance.sdk.processors.action;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

import eu.neosurance.sdk.NSRAuth;
import eu.neosurance.sdk.NSRSecurityResponse;
import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.processors.ProcessorManager;
import eu.neosurance.sdk.processors.auth.AuthProcessor;
import eu.neosurance.sdk.processors.request.RequestProcessor;
import eu.neosurance.sdk.utils.PrecheckUtils;


public class ActionProcessor {
    private static final String TAG = ActionProcessor.class.getCanonicalName();
    private final AuthProcessor authProcessor;
    private final RequestProcessor requestProcessor;
    private final DataManager dataManager;


    public ActionProcessor(AuthProcessor authProcessor, RequestProcessor requestProcessor, DataManager dataManager) {
        this.authProcessor = authProcessor;
        this.requestProcessor = requestProcessor;
        this.dataManager = dataManager;
    }

    public void sendAction(final String name, final String policyCode, final String details) {
        PrecheckUtils.guaranteeMinimalAndroidVersion();

        Log.d(TAG, "sendAction - name: " + name + " policyCode: " + policyCode + " details: " + details);
        try {
            authProcessor.authorize(new NSRAuth() {
                public void authorized(boolean authorized) throws Exception {
                    JSONObject requestPayload = new JSONObject();

                    requestPayload.put("action", name);
                    requestPayload.put("code", policyCode);
                    requestPayload.put("details", details);
                    requestPayload.put("timezone", TimeZone.getDefault().getID());
                    requestPayload.put("action_time", System.currentTimeMillis());

                    JSONObject headers = new JSONObject();
                    String token = dataManager.getAuthRepository().getToken();
                    Log.d(TAG, "sendAction token: " + token);
                    headers.put("ns_token", token);
                    headers.put("ns_lang", dataManager.getSettingsRepository().getLang());

                    requestProcessor.performSecureRequest("trace", requestPayload, headers, new RequestProcessor.RequestListener() {
                        @Override
                        public void onRequestSuccess(JSONObject json) throws Exception {
                            Log.d(TAG, "sendAction: " + json.toString());
                        }

                        @Override
                        public void onRequestFailure(String error) throws JSONException {
                            Log.e(TAG, "sendAction: " + error);

                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "sendAction", e);
        }
    }
}
