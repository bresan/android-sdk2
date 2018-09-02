package eu.neosurance.sdk.processors.request;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import eu.neosurance.sdk.NSRSecurityDelegate;
import eu.neosurance.sdk.NSRSecurityResponse;

public class RequestProcessor {
    private static final String TAG = RequestProcessor.class.getCanonicalName();

    private final Context context;
    private NSRSecurityDelegate securityDelegate;

    public RequestProcessor(Context context) {
        this.context = context;
    }

    public void setSecurityDelegate(NSRSecurityDelegate securityDelegate) {
        this.securityDelegate = securityDelegate;
    }

    public void performSecureRequest(String endpoint, JSONObject payload, JSONObject headers,
                                     final RequestListener listener) throws Exception {
        securityDelegate.secureRequest(context, endpoint, payload, headers, new NSRSecurityResponse() {
            public void completionHandler(JSONObject json, String error) throws Exception {
                if (error == null) {
                    listener.onRequestSuccess(json);
                } else {
                    listener.onRequestFailure(error);
                    Log.e(TAG, "sendEvent secureRequest: " + error);
                }
            }
        });
    }

    public interface RequestListener {
        void onRequestSuccess(JSONObject json) throws Exception;
        void onRequestFailure(String error) throws JSONException;
    }
}
