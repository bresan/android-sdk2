package eu.neosurance.sdk.auth;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.NSRAuth;
import eu.neosurance.sdk.NSRSecurityDelegate;
import eu.neosurance.sdk.NSRSecurityResponse;
import eu.neosurance.sdk.NSRUser;

public class AuthManager {

    private static final String TAG = AuthManager.class.getCanonicalName();
    private final Context context;
    private final NSRSecurityDelegate securityDelegate;

    private final AuthListener authListener;


    public AuthManager(Context context,
                       NSRSecurityDelegate securityDelegate,
                       AuthListener authListener) {
        this.context = context;
        this.securityDelegate = securityDelegate;
        this.authListener = authListener;
    }

    public void authorize(final NSRAuth delegate, final AuthArguments arguments) throws Exception {
        Log.d(TAG, "authorize");
        JSONObject auth = arguments.getAuth();
        if (auth != null && (auth.getLong("expire") - System.currentTimeMillis()) > 0) {
            delegate.authorized(true);
        } else {
            NSRUser user = arguments.getUser();
            JSONObject settings = arguments.getSettings();
            if (user != null && settings != null) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("user_code", user.getCode());
                    payload.put("code", settings.getString("code"));
                    payload.put("secret_key", settings.getString("secret_key"));

                    JSONObject sdkPayload = new JSONObject();
                    sdkPayload.put("version", arguments.getVersion());
                    sdkPayload.put("dev", settings.getInt("dev_mode"));
                    sdkPayload.put("os", arguments.getOs());
                    payload.put("sdk", sdkPayload);

                    securityDelegate.secureRequest(context, "authorize", payload, null, new NSRSecurityResponse() {
                        public void completionHandler(JSONObject response, String error) throws Exception {
                            if (error == null) {
                                JSONObject auth = response.getJSONObject("auth");
                                Log.d(TAG, "authorize auth: " + auth);
                                authListener.setAuth(auth);

                                JSONObject oldConf = arguments.getConf();
                                JSONObject conf = response.getJSONObject("conf");
                                Log.d(TAG, "authorize conf: " + conf);
                                authListener.setConf(conf);

                                String appUrl = response.getString("app_url");
                                Log.d(TAG, "authorize appUrl: " + appUrl);
                                authListener.setAppURL(appUrl);

                                if (authListener.needsInitJob(conf, oldConf)) {
                                    Log.d(TAG, "authorize needsInitJob");
                                    authListener.initJob();
                                }
                                if (conf.has("local_tracking") && conf.getBoolean("local_tracking")) {
                                    authListener.synchEventWebView();
                                }
                                delegate.authorized(true);
                            } else {
                                delegate.authorized(false);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "authorize", e);
                    delegate.authorized(false);
                }
            }
        }
    }
}
