package eu.neosurance.sdk.processors.auth;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import eu.neosurance.sdk.Constants;
import eu.neosurance.sdk.NSRAuth;
import eu.neosurance.sdk.NSRSecurityDelegate;
import eu.neosurance.sdk.NSRSecurityResponse;
import eu.neosurance.sdk.NSRUser;
import eu.neosurance.sdk.data.DataManager;
import eu.neosurance.sdk.webview.EventWebViewManager;

public class AuthProcessor {

    private static final String TAG = AuthProcessor.class.getCanonicalName();
    private final Context context;

    private final DataManager dataManager;
    private final EventWebViewManager eventWebViewManager;
    private eu.neosurance.sdk.processors.auth.AuthListener authListener;

    private NSRSecurityDelegate securityDelegate;

    public AuthProcessor(Context context,
                         DataManager dataManager,
                         EventWebViewManager eventWebViewManager,
                         eu.neosurance.sdk.processors.auth.AuthListener authListener) {
        this.context = context;
        this.dataManager = dataManager;
        this.eventWebViewManager = eventWebViewManager;
        this.authListener = authListener;
    }

    public void setSecurityDelegate(NSRSecurityDelegate securityDelegate) {
        this.securityDelegate = securityDelegate;
    }

    public void authorize() throws Exception {
        authorize(new NSRAuth() {
            @Override
            public void authorized(boolean authorized) throws Exception {

            }
        });
    }

    public void authorize(final NSRAuth delegate) throws Exception {
        final AuthArguments arguments = makeAuthArguments();
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
                                dataManager.getAuthRepository().setAuth(auth);

                                JSONObject oldConf = arguments.getConf();
                                JSONObject conf = response.getJSONObject("conf");
                                Log.d(TAG, "authorize conf: " + conf);
                                dataManager.getConfigurationRepository().setConf(conf);

                                String appUrl = response.getString("app_url");
                                Log.d(TAG, "authorize appUrl: " + appUrl);
                                dataManager.getAppUrlRepository().setAppURL(appUrl);

                                if (authListener.needsInitJob(conf, oldConf)) {
                                    Log.d(TAG, "authorize needsInitJob");
                                    authListener.initJob();
                                }

                                if (conf.has("local_tracking") && conf.getBoolean("local_tracking")) {
                                    eventWebViewManager.synchEventWebView();
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

    public AuthArguments makeAuthArguments() {
        AuthArguments authArguments = new AuthArguments.Builder()
                .conf(dataManager.getConfigurationRepository().getConf())
                .auth(dataManager.getAuthRepository().getAuth())
                .settings(dataManager.getSettingsRepository().getSettings())
                .user(dataManager.getUserRepository().getUser())
                .version(Constants.getVersion())
                .os(Constants.getOs())
                .build();

        return authArguments;
    }

    public interface AuthListener {

    }

}
